package org.fabricate.locale;

import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import org.fabricate.spi.LocaleData;

/** Korean locale — Hangul test data. */
public final class KoreanLocale implements LocaleData {

    public static final KoreanLocale INSTANCE = new KoreanLocale();

    private static final List<String> FIRST_NAMES = List.of(
            "민준", "서준", "도윤", "예준", "시우",
            "주원", "하준", "지호", "지후", "준서",
            "서연", "서윤", "지우", "하윤", "민서",
            "수아", "예은", "예나", "지유", "수빈",
            "민지", "수민", "지민", "은서", "윤서");

    private static final List<String> LAST_NAMES = List.of(
            "김", "이", "박", "최", "정",
            "강", "조", "윤", "장", "임",
            "한", "오", "서", "신", "권",
            "황", "안", "송", "류", "전",
            "홍", "고", "문", "양", "손",
            "배", "백", "허", "유", "남");

    private static final List<String> EMAIL_DOMAINS = List.of(
            "naver.com", "daum.net", "kakao.com", "hanmail.net", "nate.com",
            "korea.com", "gmail.com", "yahoo.co.kr");

    private static final List<String> PHONE_COUNTRY_CODES = List.of("+82");

    private static final List<String> STREETS = List.of(
            "강남대로", "테헤란로", "역삼로", "선릉로", "삼성로",
            "올림픽로", "송파대로", "잠실로", "한강로", "이태원로",
            "명동길", "종로", "을지로", "퇴계로", "남산길",
            "광화문로", "세종대로", "인사동길", "북촌로", "삼청로");

    private static final List<String> CITIES = List.of(
            "서울", "부산", "대구", "인천", "광주",
            "대전", "울산", "세종", "수원", "고양",
            "용인", "성남", "부천", "안산", "안양",
            "남양주", "화성", "전주", "청주", "천안");

    private static final List<String> STATES = List.of(
            "서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시",
            "대전광역시", "울산광역시", "경기도", "강원도", "충청북도",
            "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치도");

    private static final List<String> POSTAL_CODES = List.of(
            "06000", "06100", "06200", "06300", "06400",
            "04500", "04600", "04700", "04800", "04900",
            "07000", "07100", "07200", "07300", "07400");

    private static final List<String> COUNTRIES = List.of(
            "대한민국", "일본", "중국", "대만", "홍콩",
            "싱가포르", "말레이시아", "태국", "베트남", "필리핀",
            "인도네시아", "호주", "뉴질랜드", "미국", "캐나다",
            "영국", "프랑스", "독일", "이탈리아", "스페인");

    private static final List<String> JOB_TITLES = List.of(
            "소프트웨어 개발자", "프로젝트 매니저", "디자이너", "영업사원",
            "마케팅 담당자", "회계사", "변호사", "의사", "간호사",
            "교사", "엔지니어", "연구원", "편집자", "번역가",
            "통역사", "기자", "건축가", "작가", "데이터 과학자",
            "머신러닝 엔지니어", "백엔드 개발자", "프론트엔드 개발자",
            "데브옵스 엔지니어", "보안 전문가", "QA 엔지니어");

    private static final UnaryOperator<String> EMAIL_LOCAL_PART = s ->
            s.codePoints().mapToObj(Integer::toString).collect(Collectors.joining());

    private KoreanLocale() {}

    @Override public Locale locale() { return Locale.KOREAN; }
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
