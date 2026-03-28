-- 题目种子数据 批次1（题目1-5）
-- @author cheems

USE coj;

-- 题目1：整数反转
INSERT INTO question (title, content, tags, answer, judgeCase, judgeConfig, userId)
VALUES ('整数反转',
        '给你一个 32 位的有符号整数 `x`，返回将 `x` 中的数字部分反转后的结果。\n\n如果反转后整数超过 32 位的有符号整数的范围 `[-2^31, 2^31 - 1]`，就返回 0。\n\n**示例 1：**\n\n```\n输入：x = 123\n输出：321\n```\n\n**示例 2：**\n\n```\n输入：x = -123\n输出：-321\n```\n\n**示例 3：**\n\n```\n输入：x = 120\n输出：21\n```\n\n**提示：**\n\n- `-2^31 <= x <= 2^31 - 1`',
        '["数学", "字符串"]',
        '逐位取余反转，每次反转前检查是否会溢出 32 位范围。用 `rev = rev * 10 + x % 10`，并在操作前判断 `rev > INT_MAX/10` 或 `rev < INT_MIN/10`。时间复杂度 O(log x)。',
        '[{"input": "123", "output": "321"}, {"input": "-123", "output": "-321"}, {"input": "120", "output": "21"}, {"input": "0", "output": "0"}]',
        '{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 256}',
        2035918102435860500);

-- 题目2：最长公共前缀
INSERT INTO question (title, content, tags, answer, judgeCase, judgeConfig, userId)
VALUES ('最长公共前缀',
        '编写一个函数来查找字符串数组中的最长公共前缀。\n\n如果不存在公共前缀，返回空字符串 `""`。\n\n**示例 1：**\n\n```\n输入：strs = ["flower","flow","flight"]\n输出："fl"\n```\n\n**示例 2：**\n\n```\n输入：strs = ["dog","racecar","car"]\n输出：""\n解释：输入不存在公共前缀。\n```\n\n**提示：**\n\n- `1 <= strs.length <= 200`\n- `0 <= strs[i].length <= 200`\n- `strs[i]` 仅由小写英文字母组成',
        '["字符串", "字典树"]',
        '以第一个字符串为基准，逐字符与其余字符串对应位置比较，遇到不匹配或超出长度时截断返回。时间复杂度 O(S)，S 为所有字符串字符数之和。',
        '[{"input": "3\\nflower flow flight", "output": "fl"}, {"input": "3\\ndog racecar car", "output": ""}, {"input": "1\\nalone", "output": "alone"}]',
        '{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 256}',
        2035918102435860500);

-- 题目3：有效的括号
INSERT INTO question (title, content, tags, answer, judgeCase, judgeConfig, userId)
VALUES ('有效的括号',
        '给定一个只包括 `(`，`)`，`{`，`}`，`[`，`]` 的字符串 `s`，判断字符串是否有效。\n\n有效字符串需满足：\n\n1. 左括号必须用相同类型的右括号闭合。\n2. 左括号必须以正确的顺序闭合。\n3. 每个右括号都有一个对应的相同类型的左括号。\n\n**示例 1：**\n\n```\n输入：s = "()"\n输出：true\n```\n\n**示例 2：**\n\n```\n输入：s = "()[]{}"\n输出：true\n```\n\n**示例 3：**\n\n```\n输入：s = "(]"\n输出：false\n```\n\n**提示：**\n\n- `1 <= s.length <= 10^4`\n- `s` 仅由括号 `()[]{}` 组成',
        '["栈", "字符串"]',
        '使用栈。遇到左括号入栈，遇到右括号时检查栈顶是否为对应左括号，不匹配则返回 false。最终栈为空则有效。时间复杂度 O(n)，空间复杂度 O(n)。',
        '[{"input": "()", "output": "true"}, {"input": "()[]{}", "output": "true"}, {"input": "(]", "output": "false"}, {"input": "([)]", "output": "false"}, {"input": "{[]}", "output": "true"}]',
        '{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 256}',
        2035918102435860500);

-- 题目4：合并两个有序链表
INSERT INTO question (title, content, tags, answer, judgeCase, judgeConfig, userId)
VALUES ('合并两个有序链表',
        '将两个升序链表合并为一个新的**升序**链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。\n\n链表节点以空格分隔，`-1` 表示链表结束。\n\n**示例 1：**\n\n```\n输入：\n1 2 4 -1\n1 3 4 -1\n输出：1 1 2 3 4 4\n```\n\n**示例 2：**\n\n```\n输入：\n-1\n-1\n输出：\n```\n\n**示例 3：**\n\n```\n输入：\n-1\n0 -1\n输出：0\n```\n\n**提示：**\n\n- 两个链表的节点数目范围是 `[0, 50]`\n- `-100 <= Node.val <= 100`\n- `l1` 和 `l2` 均按**非递减顺序**排列',
        '["递归", "链表"]',
        '递归或迭代均可。迭代法：维护哑节点 dummy，比较两链表头节点，将较小的接到 dummy 后，直到某链表为空后将另一链表剩余部分直接接上。时间复杂度 O(m+n)。',
        '[{"input": "1 2 4 -1\\n1 3 4 -1", "output": "1 1 2 3 4 4"}, {"input": "-1\\n-1", "output": ""}, {"input": "-1\\n0 -1", "output": "0"}]',
        '{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 256}',
        2035918102435860500);

-- 题目5：爬楼梯
INSERT INTO question (title, content, tags, answer, judgeCase, judgeConfig, userId)
VALUES ('爬楼梯',
        '假设你正在爬楼梯。需要 `n` 阶你才能到达楼顶。\n\n每次你可以爬 `1` 或 `2` 个台阶。你有多少种不同的方法可以爬到楼顶呢？\n\n**示例 1：**\n\n```\n输入：n = 2\n输出：2\n解释：有两种方法可以爬到楼顶。\n1. 1 阶 + 1 阶\n2. 2 阶\n```\n\n**示例 2：**\n\n```\n输入：n = 3\n输出：3\n解释：有三种方法可以爬到楼顶。\n1. 1 阶 + 1 阶 + 1 阶\n2. 1 阶 + 2 阶\n3. 2 阶 + 1 阶\n```\n\n**提示：**\n\n- `1 <= n <= 45`',
        '["动态规划", "记忆化搜索", "数学"]',
        '经典 DP 问题，等同于斐波那契数列。`dp[i] = dp[i-1] + dp[i-2]`，`dp[1]=1, dp[2]=2`。可进一步优化为 O(1) 空间，只保留前两项。时间复杂度 O(n)。',
        '[{"input": "2", "output": "2"}, {"input": "3", "output": "3"}, {"input": "1", "output": "1"}, {"input": "10", "output": "89"}, {"input": "45", "output": "1836311903"}]',
        '{"timeLimit": 1000, "memoryLimit": 256, "stackLimit": 256}',
        2035918102435860500);
