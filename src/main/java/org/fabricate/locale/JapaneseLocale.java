package org.fabricate.locale;

import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.fabricate.spi.LocaleData;

/** Japanese locale — Kanji, Hiragana and Katakana test data. */
public final class JapaneseLocale implements LocaleData {

    public static final JapaneseLocale INSTANCE = new JapaneseLocale();

    private static final List<String> FIRST_NAMES = List.of(
            "翔太", "大輝", "陽斗", "蓮", "湊",
            "悠真", "颯", "樹", "陸", "海斗",
            "さくら", "ひまり", "凛", "結愛", "陽菜",
            "美咲", "莉子", "心春", "葵", "結菜",
            "健太", "拓海", "悠", "颯太", "翼",
            "美羽", "あかり", "千尋", "彩花", "杏奈");

    private static final List<String> LAST_NAMES = List.of(
            "佐藤", "鈴木", "高橋", "田中", "伊藤",
            "渡辺", "山本", "中村", "小林", "加藤",
            "吉田", "山田", "佐々木", "山口", "松本",
            "井上", "木村", "林", "斎藤", "清水",
            "山崎", "森", "池田", "橋本", "阿部",
            "石川", "山下", "中島", "石井", "小川");

    private static final List<String> EMAIL_DOMAINS = List.of(
            "yahoo.co.jp", "docomo.ne.jp", "softbank.jp", "ezweb.ne.jp",
            "nifty.com", "ocn.ne.jp", "biglobe.ne.jp", "so-net.ne.jp", "gmail.com");

    private static final List<String> PHONE_COUNTRY_CODES = List.of("+81");

    private static final List<String> STREETS = List.of(
            "渋谷", "新宿", "原宿", "表参道", "秋葉原",
            "銀座", "六本木", "青山", "恵比寿", "中目黒",
            "池袋", "上野", "浅草", "日本橋", "丸の内",
            "三軒茶屋", "下北沢", "自由が丘", "二子玉川", "代官山");

    private static final List<String> CITIES = List.of(
            "東京", "横浜", "大阪", "名古屋", "札幌",
            "神戸", "京都", "福岡", "川崎", "さいたま",
            "広島", "仙台", "北九州", "千葉", "堺",
            "新潟", "浜松", "熊本", "相模原", "岡山");

    private static final List<String> STATES = List.of(
            "東京都", "大阪府", "京都府", "北海道",
            "神奈川県", "愛知県", "福岡県", "兵庫県", "千葉県",
            "埼玉県", "静岡県", "広島県", "茨城県", "新潟県",
            "宮城県", "長野県", "岐阜県", "栃木県", "群馬県", "岡山県");

    private static final List<String> POSTAL_CODES = List.of(
            "100-0001", "150-0001", "160-0001", "170-0001", "180-0001",
            "530-0001", "541-0001", "600-0001", "604-0001", "605-0001",
            "060-0001", "064-0001", "220-0001", "231-0001", "240-0001");

    private static final List<String> COUNTRIES = List.of(
            "日本", "韓国", "中国", "台湾", "香港",
            "シンガポール", "マレーシア", "タイ", "ベトナム", "フィリピン",
            "インドネシア", "オーストラリア", "ニュージーランド", "アメリカ", "カナダ");

    private static final List<String> JOB_TITLES = List.of(
            "ソフトウェアエンジニア", "プロジェクトマネージャー", "デザイナー",
            "営業部長", "マーケティング担当", "経理担当", "人事担当",
            "会計士", "弁護士", "医師", "看護師", "教師",
            "技術者", "研究者", "編集者", "翻訳者", "通訳者",
            "ジャーナリスト", "建築家", "作家",
            "データサイエンティスト", "機械学習エンジニア", "フロントエンド開発者",
            "バックエンド開発者", "インフラエンジニア");

    private static final UnaryOperator<String> EMAIL_LOCAL_PART = s ->
            s.codePoints().mapToObj(Integer::toString).collect(Collectors.joining());

    private JapaneseLocale() {}

    @Override public Locale locale() { return Locale.JAPANESE; }
    @Override public String nameDelimiter() { return ""; }
    @Override public UnaryOperator<String> emailLocalPartTransform() { return EMAIL_LOCAL_PART; }
    @Override public List<String> firstNames() { return FIRST_NAMES; }
    @Override public List<String> lastNames() { return LAST_NAMES; }
    @Override public List<String> emailDomains() { return EMAIL_DOMAINS; }
    @Override public List<String> phoneCountryCodes() { return PHONE_COUNTRY_CODES; }
    @Override public List<String> streets() { return STREETS; }
    @Override public List<String> cities() { return CITIES; }
    @Override public List<String> states() { return STATES; }
    @Override public List<String> postalCodes() { return POSTAL_CODES; }
    @Override public List<String> countries() { return COUNTRIES; }
    @Override public List<String> jobTitles() { return JOB_TITLES; }
}
