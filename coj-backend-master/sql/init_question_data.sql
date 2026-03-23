-- 题目初始化数据
-- @author cheems

USE coj;

-- 题目1：两数之和
INSERT INTO question (title, content, tags, answer, judgeCase, judgeConfig, userId)
VALUES (
    '两数之和',
    '给定一个整数数组 `nums` 和一个整数目标值 `target`，请你在该数组中找出**和为目标值** `target` 的那**两个**整数，并返回它们的数组下标。\n\n你可以假设每种输入只会对应一个答案，并且你不能使用两次相同的元素。\n\n你可以按任意顺序返回答案。\n\n**示例 1：**\n\n```\n输入：nums = [2,7,11,15], target = 9\n输出：[0,1]\n解释：因为 nums[0] + nums[1] == 9 ，返回 [0, 1] 。\n```\n\n**示例 2：**\n\n```\n输入：nums = [3,2,4], target = 6\n输出：[1,2]\n```\n\n**示例 3：**\n\n```\n输入：nums = [3,3], target = 6\n输出：[0,1]\n```\n\n**提示：**\n\n- `2 <= nums.length <= 10^4`\n- `-10^9 <= nums[i] <= 10^9`\n- `-10^9 <= target <= 10^9`\n- **只会存在一个有效答案**',
    '["数组", "哈希表"]',
    '使用哈希表，遍历数组时记录每个元素及其下标。对于当前元素 `nums[i]`，查找哈希表中是否存在 `target - nums[i]`，若存在则返回两个下标。时间复杂度 O(n)，空间复杂度 O(n)。',
    '[{"input": "4\\n2 7 11 15\\n9", "output": "0 1"}, {"input": "3\\n3 2 4\\n6", "output": "1 2"}, {"input": "2\\n3 3\\n6", "output": "0 1"}]',
    '{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 256}',
    2035918102435860500
);
