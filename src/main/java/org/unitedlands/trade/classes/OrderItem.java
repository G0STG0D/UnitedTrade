package org.unitedlands.trade.classes;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.unitedlands.UnitedLib;

public class OrderItem {
    private ItemStack item;
    private int minAmount;
    private int maxAmount;
    private double price = 0d;

    public OrderItem() {
    }

    public OrderItem(ItemStack item, int minAmount, int maxAmount, double price) {
        this.item = item;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.price = price;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMinPayout() {
        return minAmount * price;
    }

    public double getMaxPayout() {
        return maxAmount * price;
    }

    public int missingAmount(Inventory inventory) {
        var itemFactory = UnitedLib.getInstance().getItemFactory();
        var orderItemId = itemFactory.getFilterName(this.item);
        int total = countMatching(inventory.getStorageContents(), orderItemId);
        return Math.max(0, this.minAmount - total);
    }

    private int countMatching(ItemStack[] contents, String orderItemId) {
        var itemFactory = UnitedLib.getInstance().getItemFactory();
        int total = 0;

        for (ItemStack stack : contents) {
            if (stack == null || stack.getType() == Material.AIR) {
                continue;
            }

            var itemId = itemFactory.getFilterName(stack);
            if (itemId.equals(orderItemId)) {
                total += stack.getAmount();
            }

            if (stack.getItemMeta() instanceof BlockStateMeta blockStateMeta
                    && blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
                total += countMatching(shulkerBox.getInventory().getContents(), orderItemId);
            }
        }
        return total;
    }

    public int removeFromInventory(Inventory inventory) {
        var itemFactory = UnitedLib.getInstance().getItemFactory();
        var orderItemId = itemFactory.getFilterName(this.item);

        ItemStack[] contents = inventory.getStorageContents();

        // First pass: count total available (including inside shulker boxes)
        int total = countMatching(contents, orderItemId);

        if (total < this.minAmount)
            return -1;

        // Second pass: remove up to maxAmount
        int remaining = Math.min(total, this.maxAmount);
        int removed = remaining;

        remaining = removeMatching(contents, orderItemId, remaining);

        inventory.setStorageContents(contents);
        return removed - remaining;
    }

    private int removeMatching(ItemStack[] contents, String orderItemId, int remaining) {
        var itemFactory = UnitedLib.getInstance().getItemFactory();

        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack stack = contents[i];

            if (stack == null || stack.getType() == Material.AIR)
                continue;

            var itemId = itemFactory.getFilterName(stack);

            if (itemId.equals(orderItemId)) {
                if (stack.getAmount() <= remaining) {
                    remaining -= stack.getAmount();
                    contents[i] = null;
                    continue;
                } else {
                    stack.setAmount(stack.getAmount() - remaining);
                    remaining = 0;
                    break;
                }
            }

            if (stack.getItemMeta() instanceof BlockStateMeta blockStateMeta
                    && blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox) {

                ItemStack[] shulkerContents = shulkerBox.getInventory().getContents();
                int before = remaining;
                remaining = removeMatching(shulkerContents, orderItemId, remaining);

                if (remaining != before) {
                    shulkerBox.getInventory().setContents(shulkerContents);
                    blockStateMeta.setBlockState(shulkerBox);
                    stack.setItemMeta(blockStateMeta);
                }
            }
        }

        return remaining;
    }
}
