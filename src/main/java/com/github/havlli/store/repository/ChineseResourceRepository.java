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

    String[] streets = {
            "Nanjing Road", "Chang'an Avenue", "Wangfujing", "Huaihai Road", "Qianmen Street",
            "Shibuya Crossing", "Ginza Street", "Harajuku Street", "Omotesando Avenue", "Akihabara Street",
            "Orchard Road", "Marina Boulevard", "Sukhumvit Road", "Silom Road", "Charoen Krung Road",
            "Myeongdong Street", "Insadong Street", "Gangnam-daero", "Itaewon-ro", "Jong-ro",
            "Lan Kwai Fong", "Nathan Road", "Connaught Road", "Des Voeux Road", "Queen's Road",
            "Bukit Bintang Street", "Jalan Alor", "Petaling Street", "Chulia Street", "Gurney Drive"
    };

    String[] cities = {
            "Beijing", "Shanghai", "Guangzhou", "Shenzhen", "Chengdu",
            "Tokyo", "Osaka", "Kyoto", "Nagoya", "Sapporo",
            "Seoul", "Busan", "Incheon", "Daegu", "Gwangju",
            "Bangkok", "Chiang Mai", "Phuket", "Pattaya", "Krabi",
            "Kuala Lumpur", "George Town", "Johor Bahru", "Kuching", "Kota Kinabalu",
            "Singapore", "Jakarta", "Bali", "Surabaya", "Bandung",
            "Hanoi", "Ho Chi Minh City", "Da Nang", "Hue", "Hoi An",
            "Manila", "Cebu", "Davao", "Quezon City", "Baguio",
            "New Delhi", "Mumbai", "Bangalore", "Chennai", "Kolkata"
    };

    String[] countries = {
            "China", "Japan", "South Korea", "North Korea",
            "India", "Pakistan", "Bangladesh", "Nepal", "Bhutan",
            "Sri Lanka", "Maldives",
            "Thailand", "Vietnam", "Cambodia", "Laos", "Myanmar",
            "Malaysia", "Singapore", "Indonesia", "Philippines",
            "Brunei", "East Timor",
            "Mongolia",
            "Kazakhstan", "Uzbekistan", "Turkmenistan", "Kyrgyzstan", "Tajikistan",
            "Afghanistan", "Iran", "Iraq", "Syria", "Jordan",
            "Lebanon", "Israel", "Saudi Arabia", "Yemen", "Oman",
            "United Arab Emirates", "Qatar", "Bahrain", "Kuwait"
    };

    String[] postalCodes = {
            "100000", "200000", "510000", "518000", "610000",
            "100-0001", "150-0001", "530-0001", "460-0001", "060-0001",
            "03000", "07300", "10000", "41500", "61400",
            "110001", "400001", "560001", "600001", "700001",
            "10100", "10200", "10300", "10400", "10500",
            "50000", "10150", "80000", "93000", "88000",
            "018956", "238858", "307987", "408600", "738734",
            "10110", "55111", "60111", "75111", "80111",
            "1000", "2000", "3000", "4000", "5000",
            "100000", "200000", "300000", "700000", "800000"
    };

    String[] states = {
            "Guangdong", "Shandong", "Henan", "Sichuan", "Jiangsu",
            "Tokyo", "Kanagawa", "Osaka", "Aichi", "Hokkaido",
            "Maharashtra", "Tamil Nadu", "Uttar Pradesh", "West Bengal", "Karnataka",
            "Seoul", "Busan", "Incheon", "Daegu", "Gyeonggi-do",
            "Bangkok", "Chiang Mai", "Phuket", "Khon Kaen", "Nakhon Ratchasima",
            "Kuala Lumpur", "Selangor", "Johor", "Penang", "Sabah",
            "Jakarta", "West Java", "Central Java", "East Java", "Bali",
            "Metro Manila", "Cebu", "Davao", "Palawan", "Iloilo",
            "Ho Chi Minh City", "Hanoi", "Da Nang", "Haiphong", "Can Tho"
    };

    String[] jobTitles = {
            "软件开发工程师", "市场经理", "财务分析师", "人力资源协调员",
            "数据科学家", "运营经理", "客户服务代表", "销售执行",
            "项目经理", "平面设计师", "网页开发员", "IT支持专家",
            "内容编写员", "用户体验设计师", "商务分析师", "环境顾问",
            "公关专员", "供应链分析师", "医疗管理者", "社交媒体经理",
            "土木工程师", "产品经理", "质量保证工程师", "投资银行家",
            "网络工程师", "数字营销专家", "SEO策略师", "制药销售代表",
            "工业设计师", "物流协调员", "建筑工程师", "活动策划",
            "软件测试工程师", "法律助理", "儿科护士", "建筑项目经理",
            "艺术指导", "合规官员", "房地产代理", "云解决方案架构师",
            "网络安全分析师", "生物技术研究科学家", "人工智能开发者", "兽医技术员",
            "机械工程师", "电气工程师", "营养师", "职业治疗师",
            "语言治疗师", "航空工程师"
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

    @Override
    public String[] getStreets() {
        return streets;
    }

    @Override
    public String[] getCities() {
        return cities;
    }

    @Override
    public String[] getCountries() {
        return countries;
    }

    @Override
    public String[] getStates() {
        return states;
    }

    @Override
    public String[] getPostalCodes() {
        return postalCodes;
    }

    @Override
    public String[] getJobTitles() {
        return jobTitles;
    }
}
