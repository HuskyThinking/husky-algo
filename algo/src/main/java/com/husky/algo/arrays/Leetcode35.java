package com.husky.algo.arrays;

/**
 * @author li_dingxin
 * @date 2025/9/12
 * <a href="https://leetcode.cn/problems/search-insert-position/">...</a>
 */
public class Leetcode35 {

    public int searchInsert(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            int mid = left + ((right - left) >> 1);
            if (nums[mid] > target) {
                right = mid - 1;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                return mid;
            }
        }
        return right + 1;
    }
}
