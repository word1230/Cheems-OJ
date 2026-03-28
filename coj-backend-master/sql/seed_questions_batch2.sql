-- 题目种子数据 批次2（题目6-10）
-- @author cheems

USE coj;

-- 题目6：二分查找
INSERT INTO question (title, content, tags, answer, judgeCase, judgeConfig, userId)
VALUES ('二分查找',
        '给定一个 `n` 个元素有序的（升序）整型数组 `nums` 和一个目标值 `target`，写一个函数搜索 `nums` 中的 `target`，如果目标值存在返回下标，否则返回 `-1`。\n\n**示例 1：**\n\n```\n输入：nums = [-1,0,3,5,9,12], target = 9\n输出：4\n解释：9 出现在 nums 中并且下标为 4\n```\n\n**示例 2：**\n\n```\n输入：nums = [-1,0,3,5,9,12], target = 2\n输出：-1\n解释：2 不存在 nums 中因此返回 -1\n```\n\n**提示：**\n\n- 你可以假设 `nums` 中的所有元素是不重复的。\n- `n` 将在 `[1, 10000]` 之间。\n- `nums` 的每个元素都将在 `[-9999, 9999]` 之间。',
        '["数组", "二分查找"]',
        '标准二分查找：维护 left、right 指针，每次取中间值 mid 与 target 比较，相等返回 mid，target 更大则 left=mid+1，否则 right=mid-1。时间复杂度 O(log n)，空间复杂度 O(1)。',
        '[{"input": "6\\n-1 0 3 5 9 12\\n9", "output": "4"}, {"input": "6\\n-1 0 3 5 9 12\\n2", "output": "-1"}, {"input": "1\\n5\\n5", "output": "0"}, {"input": "3\\n1 3 5\\n1", "output": "0"}]',
        '{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 256}',
        2035918102435860500);

-- 题目7：最大子数组和
INSERT INTO question (title, content, tags, answer, judgeCase, judgeConfig, userId)
VALUES ('最大子数组和',
        '给你一个整数数组 `nums`，请你找出一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。\n\n**子数组** 是数组中的一个连续部分。\n\n**示例 1：**\n\n```\n输入：nums = [-2,1,-3,4,-1,2,1,-5,4]\n输出：6\n解释：连续子数组 [4,-1,2,1] 的和最大，为 6。\n```\n\n**示例 2：**\n\n```\n输入：nums = [1]\n输出：1\n```\n\n**示例 3：**\n\n```\n输入：nums = [5,4,-1,7,8]\n输出：23\n```\n\n**提示：**\n\n- `1 <= nums.length <= 10^5`\n- `-10^4 <= nums[i] <= 10^4`',
        '["数组", "动态规划", "分治"]',
        'Kadane 算法：维护 `curSum`（以当前元素结尾的最大子数组和）和 `maxSum`（全局最大）。`curSum = max(nums[i], curSum + nums[i])`，`maxSum = max(maxSum, curSum)`。时间复杂度 O(n)，空间复杂度 O(1)。',
        '[{"input": "9\\n-2 1 -3 4 -1 2 1 -5 4", "output": "6"}, {"input": "1\\n1", "output": "1"}, {"input": "5\\n5 4 -1 7 8", "output": "23"}, {"input": "4\\n-2 -1 -3 -4", "output": "-1"}]',
        '{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 256}',
        2035918102435860500);

-- 题目8：买卖股票的最佳时机
INSERT INTO question (title, content, tags, answer, judgeCase, judgeConfig, userId)
VALUES ('买卖股票的最佳时机',
        '给定一个数组 `prices`，它的第 `i` 个元素 `prices[i]` 表示一支给定股票第 `i` 天的价格。\n\n你只能选择**某一天**买入这只股票，并选择在**未来的某一个不同的日子**卖出该股票。设计一个算法来计算你所能获取的最大利润。\n\n返回你可以从这笔交易中获取的最大利润。如果你不能获取任何利润，返回 `0`。\n\n**示例 1：**\n\n```\n输入：prices = [7,1,5,3,6,4]\n输出：5\n解释：在第 2 天（股票价格 = 1）的时候买入，在第 5 天（股票价格 = 6）的时候卖出，最大利润 = 6-1 = 5。\n```\n\n**示例 2：**\n\n```\n输入：prices = [7,6,4,3,1]\n输出：0\n解释：在这种情况下, 没有交易完成, 所以最大利润为 0。\n```\n\n**提示：**\n\n- `1 <= prices.length <= 10^5`\n- `0 <= prices[i] <= 10^4`',
        '["数组", "动态规划"]',
        '一次遍历：维护历史最低价 `minPrice`，对每个价格计算当天卖出的利润 `prices[i] - minPrice`，更新最大利润。时间复杂度 O(n)，空间复杂度 O(1)。',
        '[{"input": "6\\n7 1 5 3 6 4", "output": "5"}, {"input": "5\\n7 6 4 3 1", "output": "0"}, {"input": "1\\n1", "output": "0"}, {"input": "4\\n3 2 6 5", "output": "4"}]',
        '{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 256}',
        2035918102435860500);

-- 题目9：反转链表
INSERT INTO question (title, content, tags, answer, judgeCase, judgeConfig, userId)
VALUES ('反转链表',
        '给你单链表的头节点 `head`，请你反转链表，并返回反转后的链表。\n\n链表节点以空格分隔，`-1` 表示链表结束（若链表为空则只输入 `-1`）。\n\n**示例 1：**\n\n```\n输入：1 2 3 4 5 -1\n输出：5 4 3 2 1\n```\n\n**示例 2：**\n\n```\n输入：1 2 -1\n输出：2 1\n```\n\n**示例 3：**\n\n```\n输入：-1\n输出：\n```\n\n**提示：**\n\n- 链表中节点的数目范围是 `[0, 5000]`\n- `-5000 <= Node.val <= 5000`',
        '["递归", "链表"]',
        '迭代法：维护 prev（初始为 null）和 curr 指针，每次将 curr.next 指向 prev，然后 prev=curr，curr=next，直到 curr 为 null，返回 prev。时间复杂度 O(n)，空间复杂度 O(1)。',
        '[{"input": "1 2 3 4 5 -1", "output": "5 4 3 2 1"}, {"input": "1 2 -1", "output": "2 1"}, {"input": "-1", "output": ""}, {"input": "1 -1", "output": "1"}]',
        '{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 256}',
        2035918102435860500);

-- 题目10：岛屿数量
INSERT INTO question (title, content, tags, answer, judgeCase, judgeConfig, userId)
VALUES ('岛屿数量',
        '给你一个由 `\'1\'`（陆地）和 `\'0\'`（水）组成的的二维网格，请你计算网格中岛屿的数量。\n\n岛屿总是被水拦截，并且每座岛屿只能由水平方向和/或垂直方向上相邻的陆地连接形成。\n\n此外，你可以假设该网格的四条边均被水包围。\n\n**示例 1：**\n\n```\n输入：\n4 4\n1 1 1 1 0\n1 1 0 1 0\n1 1 0 0 0\n0 0 0 0 0\n输出：1\n```\n\n**示例 2：**\n\n```\n输入：\n4 5\n1 1 0 0 0\n1 1 0 0 0\n0 0 1 0 0\n0 0 0 1 1\n输出：3\n```\n\n**提示：**\n\n- `m == grid.length`\n- `n == grid[i].length`\n- `1 <= m, n <= 300`\n- `grid[i][j]` 的值为 `\'0\'` 或 `\'1\'`',
        '["深度优先搜索", "广度优先搜索", "并查集", "数组", "矩阵"]',
        'DFS 方案：遍历网格，遇到 \'1\' 时岛屿数加一，并用 DFS 将连通的所有 \'1\' 标记为已访问（置为 \'0\'），继续扫描直到遍历完整个网格。时间复杂度 O(m*n)，空间复杂度 O(m*n)（递归栈）。',
        '[{"input": "3 4\\n1 1 1 1 0\\n1 1 0 1 0\\n1 1 0 0 0\\n0 0 0 0 0", "output": "1"}, {"input": "4 5\\n1 1 0 0 0\\n1 1 0 0 0\\n0 0 1 0 0\\n0 0 0 1 1", "output": "3"}, {"input": "1 1\\n0", "output": "0"}, {"input": "1 1\\n1", "output": "1"}]',
        '{"timeLimit": 2000, "memoryLimit": 256, "stackLimit": 256}',
        2035918102435860500);
