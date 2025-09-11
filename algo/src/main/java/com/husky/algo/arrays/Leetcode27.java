package com.husky.algo.arrays;

/**
 * @author li_dingxin
 * @date 2025/9/11
 * <a href="https://leetcode.cn/problems/remove-element/description/">...</a>
 */
public class Leetcode27 {

    public int removeElement(int[] nums, int val) {
        int length = nums.length;
        int left = 0;
        int right = 0;
        for (int i = 0; i < length; i++) {
            if (nums[i] != val) {
                nums[left] = nums[right];
                left++;
            }
            right++;
        }
        return left;
    }

}
