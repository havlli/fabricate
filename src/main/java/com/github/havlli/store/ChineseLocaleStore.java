package com.github.havlli;

import java.util.Locale;

public class ChineseLocaleStore implements LocaleStore {
    @Override
    public Locale getLocale() {
        return Locale.CHINESE;
    }

    @Override
    public String[] getFirstNames() {
        return firstNames;
    }

    @Override
    public String[] getLastNames() {
        return lastNames;
    }

    private final String[] firstNames = {
            "伟",
            "敏",
            "静",
            "丽",
            "强",
            "磊",
            "军",
            "洋",
            "勇",
            "艳",
            "杰",
            "娜",
            "涛",
            "明",
            "刚",
            "平",
            "刘",
            "超",
            "松",
            "林",
            "英",
            "华",
            "慧",
            "天",
            "玲",
            "红",
            "宇",
            "浩",
            "然",
            "文",
            "东",
            "波",
            "斌",
            "萍",
            "坤"
    };

    private final String[] lastNames = {
            "李",
            "王",
            "张",
            "刘",
            "陈",
            "杨",
            "黄",
            "赵",
            "吴",
            "周",
            "徐",
            "孙",
            "马",
            "朱",
            "胡",
            "郭",
            "何",
            "高",
            "林",
            "罗",
            "郑",
            "梁",
            "谢",
            "宋",
            "唐",
            "许",
            "韩",
            "冯",
            "邓",
            "曹",
            "彭",
            "曾",
            "肖",
            "田",
            "董"
    };
}
