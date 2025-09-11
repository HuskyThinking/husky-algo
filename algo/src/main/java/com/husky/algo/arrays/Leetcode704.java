package com.husky.algo.arrays;

/**
 * @author li_dingxin
 * @date 2025/9/11
 * <a href="https://leetcode.cn/problems/binary-search/">...</a>
 */
public class Leetcode704 {

    public int search(int[] nums, int target) {
        if (target < nums[0] || target > nums[nums.length - 1]) {
            return -1;
        }
        int left = 0;
        int right = nums.length - 1;
        while (left <= right) {
            int mid = left + ((right - left) >> 1);
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] > target) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return -1;
    }

}
