package com.github.havlli.store.repository;

public class ChineseResourceRepository implements ResourceRepository {

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
            "建国",
            "志强",
            "美丽",
            "小明",
            "晓东",
            "天翔",
            "思思",
            "小芳",
            "晓琳",
            "明明",
            "丽华",
            "飞飞",
            "小强",
            "志",
            "晓静",
            "佳佳",
            "小杰",
            "欣欣",
            "小燕",
            "光辉",
            "世界",
            "佳慧",
            "天",
            "小宝",
            "晓峰",
            "志明",
            "宇航",
            "欢欢",
            "海洋",
            "小",
            "文静",
            "天天",
            "晓华",
            "婷婷",
            "小龙"
    };

    String[] emailDomains = {
            "163.com",
            "126.com",
            "qq.com",
            "sina.com",
            "sina.cn",
            "sohu.com",
            "tom.com",
            "yeah.net",
            "21cn.com",
            "aliyun.com"
    };

    String[] regionPhoneNumbers = {
            "+44", "+49", "+33", "+7", "+39", "+34", "+46", "+47", "+31", "+48", "+32", "+43", "+45", "+358", "+351", "+420", "+41", "+380", "+375"
    };

    @Override
    public String[] getFirstNames() {
        return firstNames;
    }

    @Override
    public String[] getLastNames() {
        return lastNames;
    }

    @Override
    public String[] getEmailDomains() {
        return emailDomains;
    }

    @Override
    public String[] getRegionPhoneNumbers() {
        return regionPhoneNumbers;
    }
}
