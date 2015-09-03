

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * TextConverter
 */
public class TextConverter {

	private static final Map<String, String> jptable = new LinkedHashMap<String, String>();

	static {

		jptable.put("ttsa", "っつぁ");
		jptable.put("ttsi", "っつぃ");
		jptable.put("ttsu", "っつ");
		jptable.put("ttse", "っつぇ");
		jptable.put("ttso", "っつぉ");

		jptable.put("ssha", "っしゃ");
		jptable.put("sshi", "っし");
		jptable.put("sshu", "っしゅ");
		jptable.put("sshe", "っしぇ");
		jptable.put("ssho", "っしょ");
		jptable.put("ssya", "っしゃ");
		jptable.put("ssyi", "っしぃ");
		jptable.put("ssyu", "っしゅ");
		jptable.put("ssye", "っしぇ");
		jptable.put("ssyo", "っしょ");

		jptable.put("ccha", "っちゃ");
		jptable.put("cchi", "っち");
		jptable.put("cchu", "っちゅ");
		jptable.put("cche", "っちぇ");
		jptable.put("ccho", "っちょ");
		jptable.put("ttya", "っちゃ");
		jptable.put("ttyi", "っちぃ");
		jptable.put("ttyu", "っちゅ");
		jptable.put("ttye", "っちぇ");
		jptable.put("ttyo", "っちょ");

		jptable.put("kka", "っか");
		jptable.put("kki", "っき");
		jptable.put("kku", "っく");
		jptable.put("kke", "っけ");
		jptable.put("kko", "っこ");
		jptable.put("ssa", "っさ");
		jptable.put("ssi", "っし");
		jptable.put("ssu", "っす");
		jptable.put("sse", "っせ");
		jptable.put("sso", "っそ");
		jptable.put("tta", "った");
		jptable.put("tti", "っち");
		jptable.put("ttu", "っつ");
		jptable.put("tte", "って");
		jptable.put("tto", "っと");
		jptable.put("hha", "っは");
		jptable.put("hhi", "っひ");
		jptable.put("hhu", "っふ");
		jptable.put("hhe", "っへ");
		jptable.put("hho", "っほ");
		jptable.put("ppa", "っぱ");
		jptable.put("ppi", "っぴ");
		jptable.put("ppu", "っぷ");
		jptable.put("ppe", "っぺ");
		jptable.put("ppo", "っぽ");

		jptable.put("tha", "てゃ");
		jptable.put("thi", "てぃ");
		jptable.put("thu", "てゅ");
		jptable.put("the", "てぇ");
		jptable.put("tho", "てょ");
		jptable.put("tsa", "つぁ");
		jptable.put("tsi", "つぃ");
		jptable.put("tsu", "つ");
		jptable.put("tse", "つぇ");
		jptable.put("tso", "つぉ");
		jptable.put("jya", "じゃ");
		jptable.put("jyi", "じぃ");
		jptable.put("jyu", "じゅ");
		jptable.put("jye", "じぇ");
		jptable.put("jyo", "じょ");
		jptable.put("bya", "びゃ");
		jptable.put("byi", "びぃ");
		jptable.put("byu", "びゅ");
		jptable.put("bye", "びぇ");
		jptable.put("byo", "びょ");
		jptable.put("sya", "しゃ");
		jptable.put("syi", "しぃ");
		jptable.put("syu", "しゅ");
		jptable.put("sye", "しぇ");
		jptable.put("syo", "しょ");
		jptable.put("sha", "しゃ");
		jptable.put("shi", "し");
		jptable.put("shu", "しゅ");
		jptable.put("she", "しぇ");
		jptable.put("sho", "しょ");
		jptable.put("mya", "みゃ");
		jptable.put("myi", "みぃ");
		jptable.put("myu", "みゅ");
		jptable.put("mye", "みぇ");
		jptable.put("myo", "みょ");
		jptable.put("rya", "りゃ");
		jptable.put("ryi", "りぃ");
		jptable.put("ryu", "りゅ");
		jptable.put("rye", "りぇ");
		jptable.put("ryo", "りょ");
		jptable.put("kya", "きゃ");
		jptable.put("kyi", "きぃ");
		jptable.put("kyu", "きゅ");
		jptable.put("kye", "きぇ");
		jptable.put("kyo", "きょ");
		jptable.put("gya", "ぎゃ");
		jptable.put("gyi", "ぎぃ");
		jptable.put("gyu", "ぎゅ");
		jptable.put("gye", "ぎぇ");
		jptable.put("gyo", "ぎょ");
		jptable.put("tya", "ちゃ");
		jptable.put("tyi", "ちぃ");
		jptable.put("tyu", "ちゅ");
		jptable.put("tye", "ちぇ");
		jptable.put("tyo", "ちょ");
		jptable.put("nya", "にゃ");
		jptable.put("nyi", "にぃ");
		jptable.put("nyu", "にゅ");
		jptable.put("nye", "にぇ");
		jptable.put("nyo", "にょ");
		jptable.put("hya", "ひゃ");
		jptable.put("hyi", "ひぃ");
		jptable.put("hyu", "ひゅ");
		jptable.put("hye", "ひぇ");
		jptable.put("hyo", "ひょ");
		jptable.put("ja", "じゃ");
		jptable.put("ji", "じ");
		jptable.put("ju", "じゅ");
		jptable.put("je", "じぇ");
		jptable.put("jo", "じょ");
		jptable.put("cha", "ちゃ");
		jptable.put("chi", "ち");
		jptable.put("chu", "ちゅ");
		jptable.put("che", "ちぇ");
		jptable.put("cho", "ちょ");
		jptable.put("fa", "ふぁ");
		jptable.put("fi", "ふぃ");
		jptable.put("fu", "ふ");
		jptable.put("fe", "ふぇ");
		jptable.put("fo", "ふぉ");

		jptable.put("xka", "ヵ");
		jptable.put("xke", "ヶ");
		jptable.put("xtu", "っ");
		jptable.put("xna", "んあ");
		jptable.put("xni", "ぃ");
		jptable.put("xnu", "んう");
		jptable.put("xne", "んえ");
		jptable.put("xno", "んお");
		jptable.put("xya", "ゃ");
		jptable.put("xyu", "ゅ");
		jptable.put("xyo", "ょ");
		jptable.put("xwa", "ゎ");
		jptable.put("xn", "ん");
		jptable.put("xa", "ぁ");
		jptable.put("xi", "ぃ");
		jptable.put("xu", "ぅ");
		jptable.put("xe", "ぇ");
		jptable.put("xo", "ぉ");

		jptable.put("ka", "か");
		jptable.put("ki", "き");
		jptable.put("ku", "く");
		jptable.put("ke", "け");
		jptable.put("ko", "こ");
		jptable.put("sa", "さ");
		jptable.put("si", "し");
		jptable.put("su", "す");
		jptable.put("se", "せ");
		jptable.put("so", "そ");
		jptable.put("ta", "た");
		jptable.put("ti", "ち");
		jptable.put("tu", "つ");
		jptable.put("te", "て");
		jptable.put("to", "と");
		jptable.put("na", "な");
		jptable.put("ni", "に");
		jptable.put("nu", "ぬ");
		jptable.put("ne", "ね");
		jptable.put("no", "の");
		jptable.put("ha", "は");
		jptable.put("hi", "ひ");
		jptable.put("hu", "ふ");
		jptable.put("he", "へ");
		jptable.put("ho", "ほ");
		jptable.put("ma", "ま");
		jptable.put("mi", "み");
		jptable.put("mu", "む");
		jptable.put("me", "め");
		jptable.put("mo", "も");
		jptable.put("ya", "や");
		jptable.put("yi", "い");
		jptable.put("yu", "ゆ");
		jptable.put("ye", "いぇ");
		jptable.put("yo", "よ");
		jptable.put("ra", "ら");
		jptable.put("ri", "り");
		jptable.put("ru", "る");
		jptable.put("re", "れ");
		jptable.put("ro", "ろ");
		jptable.put("wa", "わ");
		jptable.put("wi", "うぃ");
		jptable.put("wu", "う");
		jptable.put("we", "うぇ");
		jptable.put("wo", "を");

		jptable.put("nn", "ん");

		jptable.put("ga", "が");
		jptable.put("gi", "ぎ");
		jptable.put("gu", "ぐ");
		jptable.put("ge", "げ");
		jptable.put("go", "ご");
		jptable.put("za", "ざ");
		jptable.put("zi", "じ");
		jptable.put("zu", "ず");
		jptable.put("ze", "ぜ");
		jptable.put("zo", "ぞ");
		jptable.put("da", "だ");
		jptable.put("di", "ぢ");
		jptable.put("du", "づ");
		jptable.put("de", "で");
		jptable.put("do", "ど");
		jptable.put("ba", "ば");
		jptable.put("bi", "び");
		jptable.put("bu", "ぶ");
		jptable.put("be", "べ");
		jptable.put("bo", "ぼ");
		jptable.put("pa", "ぱ");
		jptable.put("pi", "ぴ");
		jptable.put("pu", "ぷ");
		jptable.put("pe", "ぺ");
		jptable.put("po", "ぽ");
		jptable.put("la", "ぁ");
		jptable.put("li", "ぃ");
		jptable.put("lu", "ぅ");
		jptable.put("le", "ぇ");
		jptable.put("lo", "ぉ");

		jptable.put("a", "あ");
		jptable.put("i", "い");
		jptable.put("u", "う");
		jptable.put("e", "え");
		jptable.put("o", "お");

		jptable.put("，", "、");
		jptable.put(",", "、");
		jptable.put("．", "。");
		jptable.put(".", "。");
	}

	public String convert(String input) {
		Set<String> keys = jptable.keySet();
		for (String key : keys) {
			int index = input.indexOf(key);
			if (index != -1) {
				return input.substring(0, index) + jptable.get(key);
			}
		}
		return input;
	}
}
