package com.coffee.editor;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mcoffee on 2018/3/20.
 * Email: mkfcoffee@163.com
 */
public class PreviewFragment extends Fragment {
    private static final String SERIALIZED = "";

    private String mSerialized;

    private OnFragmentInteractionListener mListener;

    public PreviewFragment() {
    }

    public static PreviewFragment newInstance(String serialized) {
        PreviewFragment fragment = new PreviewFragment();
        Bundle args = new Bundle();
        args.putString(SERIALIZED, serialized);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSerialized = getArguments().getString(SERIALIZED);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preview, container, false);

        Editor renderer = (Editor) view.findViewById(R.id.renderer);
        Map<Integer, String> headingTypeface = getHeadingTypeface();
        Map<Integer, String> contentTypeface = getContentface();
        renderer.setHeadingTypeface(headingTypeface);
        renderer.setContentTypeface(contentTypeface);
        renderer.setEditorImageLayout(R.layout.tmpl_image_view);
        String content = mSerialized;
//        EditorContent deserialized = renderer.getContentDeserialized(content);
//        renderer.render(deserialized);
        renderer.render(htmlExample());
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public Map<Integer, String> getHeadingTypeface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/Audiowide-Regular.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/Audiowide-Regular.ttf");
        typefaceMap.put(Typeface.ITALIC, "fonts/Audiowide-Regular.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, "fonts/Audiowide-Regular.ttf");
        return typefaceMap;
    }

    public Map<Integer, String> getContentface() {
        Map<Integer, String> typefaceMap = new HashMap<>();
        typefaceMap.put(Typeface.NORMAL, "fonts/Lato-Medium.ttf");
        typefaceMap.put(Typeface.BOLD, "fonts/Lato-Bold.ttf");
        typefaceMap.put(Typeface.ITALIC, "fonts/Lato-MediumItalic.ttf");
        typefaceMap.put(Typeface.BOLD_ITALIC, "fonts/Lato-BoldItalic.ttf");
        return typefaceMap;
    }

    private String htmlExample() {
        return "<!DOCTYPE html>\\n  <html lang=\\\"en\\\">\\n  <head><meta charset=\\\"UTF-8\\\">\\n  <meta name=\\\"viewport\\\" content=\\\"width=device-width, " +
                "initial-scale=1.0\\\">\\n  <meta http-equiv=\\\"X-UA-Compatible\\\" content=\\\"ie=edge\\\">\\n  <style>\\n    img{\\n      max-width:100%;\\n    }\\n    body{\\n      " +
                "padding:12px 4%;\\n      margin:0;\\n    }\\n    p{\\n      line-height: 27px;\\n      font-size: 17px;\\n      color:#242529;\\n      margin: 12px 0;\\n      word-wrap:break-word;\\n      word-break:break-all;\\n    }\\n  </style>\\n  </head>\\n  <body><div><p style=\\\"text-align:start;\\\">十年前一款《天龙八部》端游火爆中国网游市场，其发扬光大的宝宝繁殖养育和宝石镶嵌成为一代人的网游回忆。如今一款由《天龙八部》端游原班人马打造的《天龙八部手游》正式公测，游戏内每一处都体现着对《天龙八部》端游的致敬和创新。在本次公测后游戏现在到底有哪些改动，笔者带你一探究竟。</p>" +
                "<p style=\\\"text-align:start;\\\">简单而轻松的上手体验</p><p style=\\\"text-align:start;\\\">得力于游戏本身并不复杂的操作，游戏上手异常轻松。跟随主线剧情很快的熟悉了游戏的操作和基本玩法之后，《天龙八部手游》将游戏绝大多数的进阶玩法都交由玩家的互动了。不同类型的玩家可以随心所欲的向合适自己的方向发展，这些给予了休闲玩家和核心玩家等不同群体共同留存的可能。游戏设置了很多活动，这些副本活动如何取舍最终交给玩家决定。</p><p><a href=\\\"http://img1.gamersky.com/image2017/07/20170705_gy_385_1/gamersky_01origin_01_2017751452F6E_S.jpg\\\"><img src=\\\"http://img1.gamersky.com/image2017/07/20170705_gy_385_1/gamersky_01origin_01_2017751452F6E_S.jpg?imageView2/0/w/1080\\\" " +
                "alt=\\\"\\\"></a></p><p style=\\\"text-align:start;\\\">《天龙八部手游》中原了端游中多项经典的玩法，老三环、燕子坞、刷马刷反刷棋等等核心装备资源产出，再加上挖宝图打宋辽大战等诸多休闲玩法，每月两届的踢球也如期而至。花样繁多的活动充实了玩家的游玩体验，也带给玩家足够的经验、金钱、物品产出。</p><p style=\\\"text-align:start;\\\">游戏内高度的自由化和策略的延伸便衍生出了近乎无限的玩法种类。而游戏中人与人之间的交互又使的游戏中不确定的因素暴增。小编作为一名与世无争的和平主义者，没事就喜欢清清日常打打活动，《天龙八部手游》中的拥有丰富的精美时装、坐骑以及可爱萌宠着实照顾了每个休闲玩家的兴趣点。针对于女玩家的各种精美时装坐骑也层出不穷，穿上漂亮的时装，坐上可爱的双人坐骑和萌宠一起闯荡江湖的日子真是不要太美。</p>" +
                "<p style=\\\"text-align:start;\\\">友善的联盟与优秀的交互平台</p><p style=\\\"text-align:start;\\\">俗话说单丝不成线孤木不成林，在游戏中笔者短暂的度过了初期剧情和原始积累之后，除了自己思考外，绝大多数的游戏常识都因为拜了一个师父而变得简单有趣。</p><p><a href=\\\"http://img1.gamersky.com/image2017/07/20170705_gy_385_1/gamersky_01small_02_20177514521C1_S.jpg\\\"><img src=\\\"http://img1.gamersky.com/image2017/07/20170705_gy_385_1/gamersky_01small_02_20177514521C1_S.jpg?imageView2/0/w/1080\\\" alt=\\\"\\\"></a></p><p style=\\\"text-align:start;\\\">当玩家加入了帮会之中总会有这样或者那样的故事。无论是为了抱帮主大腿还是在游戏里找个归宿，玩家在联盟可以获得安全的庇护和来自每一个其他玩家的帮助。" +
                "没事给自己找个师父找个兄弟在天龙的江湖里纵马驰骋，在自己在帮派里里的跑跑商卖卖萌，和自己的情缘伴侣一起在西湖边做做小表情扮个鬼脸，调教一下徒弟寻找几个兄弟，这一切都让这个天龙的江湖显出别样的柔情。</p><p style=\\\"text-align:start;\\\">霸气的新职业天龙</p><p style=\\\"text-align:start;\\\">和《天龙八部》端游一样的是，新开放的职业“天龙”依旧是远程职业，远程职业在刷怪抢怪先手控制上中有着很多的优势。天龙门派身为四属性攻击门派，这种独特的混合伤害在面对任何职业的时候都有稳定的输出。其他的四个门派都是单属性攻击很容易被针对，而天龙则是四属性攻击，所以不用担心被抗性针对。天龙在游戏中输出能力不是很高，略逊于逍遥和天山，但是高于峨嵋和丐帮。</p><p style=\\\"text-align:start;\\\">作为唯一一个没有任何保命技能的门派，天龙是的生存能力在目前的版本里可以说是最弱的一个。但是天龙门派的控制技能和状态BUFF技能却是所有职业中最多的，作为一个定位为控场的门派，天龙无论从哪一方面都更倾向于PVP。</p>" +
                "<p style=\\\"text-align:start;\\\">天龙的八个职业技能可以说是全部和PVP有关系，首先是一阳指技能，每个门派的第一个技能，仅有6秒的冷却时间却是天龙门派中伤害最高的技能。玩家在花费一两周时间攒齐慕容复的碎片，得到慕容复英雄指点后，这个技能有20%的几率造成封印3秒。封印是天龙门派的技能控制效果，集合定身和散功两种控制技能效果，可让目标获得一个无法驱散的定身加无法使用技能。只看这一个技能控制效果，就可以看出天龙门派在PVP方面的恐怖实力。</p><p><a href=\\\"http://img1.gamersky.com/image2017/07/20170705_gy_385_1/gamersky_02origin_03_2017751452468_S.jpg\\\"><img src=\\\"http://img1.gamersky.com/image2017/07/20170705_gy_385_1/gamersky_02origin_03_2017751452468_S.jpg?imageView2/0/w/1080\\\" alt=\\\"\\\"></a></p><p style=\\\"text-align:start;\\\">第二个技能群攻技能三阳开泰，作为群攻最好拿到的指点还是增加三功，第三个技能八门金锁，直接造成封印效果加持续伤害。" +
                "第四个技能指点江山，造成伤害和受到伤害放大效果。第五个技能少泽剑，群攻技能，造成伤害和命中减少50%效果。第六个技能商阳剑，造成伤害和持续掉血效果。</p><p style=\\\"text-align:start;\\\">第七个技能中冲剑，造成伤害和抗性失效效果，这个技能号称是天龙八部手游中最无解的一个技能，在获得萧远山英雄指点后，可以让一个目标四种抗性全部失效10秒的时间。在PVP活动中，战力再高的玩家，抗性全部失效也和被扒光了装备一样，10秒的时间已经够死个来回了。</p><p><a href=\\\"http://img1.gamersky.com/image2017/07/20170705_gy_385_1/gamersky_02small_04_2017751452673_S.jpg\\\"><img src=\\\"http://img1.gamersky.com/image2017/07/20170705_gy_385_1/gamersky_02small_04_2017751452673_S.jpg?imageView2/0/w/1080\\\" alt=\\\"\\\"></a></p><p style=\\\"text-align:start;\\\">第八个技能是百步穿杨，增加100%的命中持续15秒，在获得乔峰指点后，还可以重置少则、商阳、中冲三个技能的冷却时间。100%给对方套上各种状态，想想就非常酸爽。</p>" +
                "<p><a href=\\\"http://img1.gamersky.com/image2017/07/20170705_gy_385_1/gamersky_03origin_05_2017751452A88_S.jpg\\\"><img src=\\\"http://img1.gamersky.com/image2017/07/20170705_gy_385_1/gamersky_03origin_05_2017751452A88_S.jpg?imageView2/0/w/1080\\\" alt=\\\"\\\"></a></p><p style=\\\"text-align:start;\\\">结语：</p><p style=\\\"text-align:start;\\\">本次公测版本的《天龙八部手游》已经颇具一个好游戏应有的素质了。在过国内一线大厂商对游戏品质的优秀把控下，《天龙八部》端游本身良好的游戏性和平衡性在本作得到了充分的继承和发扬。在上次测评里出现的BUG已经得到了相应的修复，体验上又有了长足的进步，这些都体现出了运营方对该游戏的用心。</p><p style=\\\"text-align:start;\\\">《天龙八部手游》以其善意亲民的游戏体验，丰富广大的成长养成体系和颇具创意的游戏方式都是对端游合格的致敬与发展。如果你是天龙八部端游的老玩家或者对武侠MMORPG有兴趣，" +
                "不妨约好朋友一同来再战这百态江湖。</p></div></body>\\n  </html>";
    }
}
