package com.moxin.videoline.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moxin.videoline.LiveConstant;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.FullyGridLayoutManager;
import com.moxin.videoline.adapter.GridImageAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.api.ApiUtils;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.helper.ImageUtil;
import com.moxin.videoline.inter.JsonCallback;
import com.moxin.videoline.json.JsonRequestUser;
import com.moxin.videoline.json.jsonmodle.UserData;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.UserImgModel;
import com.moxin.videoline.utils.CuckooQiniuFileUploadUtils;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.maning.imagebrowserlibrary.utils.StatusBarUtil;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import cn.qqtheme.framework.picker.NumberPicker;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;


/**
 * 编辑资料页面
 */
public class EditActivity extends BaseActivity {

    private RecyclerView redactRecycler;
    private CircleImageView headImg;
    private TextView redactNameText, redactSexText;
    private GridImageAdapter redactAdapter;
    private RelativeLayout mRlChangeNameLayout;

    private File headImgFile;//头像列表
    private ArrayList<File> filesByAll = new ArrayList<>();//要上传的图片
    private ArrayList<UserImgModel> imgLoader = new ArrayList<>();//网络图片视图列表
    private int sex = 0;//性别

    //判断信息
    private boolean isAlterSex = false;//是否可以更改性别
    private boolean isAddPlus = false;//是否存在+号

    private ArrayList<String> path = new ArrayList<>();//记录选中的图片路径

    private int selectImageType = 0;
    private TextView signTv;
    private RelativeLayout signRl;
    private String sign;

    private CuckooQiniuFileUploadUtils cuckooQiniuFileUploadUtils;
    private int selectType = PictureMimeType.ofImage();
    private List<LocalMedia> selectList = new ArrayList<>();

    @BindView(R.id.groupListView)
    QMUIGroupListView groupListView;
    private QMUICommonListItemView itemHeight;
    private QMUICommonListItemView itemWeight;
    private QMUICommonListItemView itemConstellation;
    private QMUICommonListItemView itemIntroduce;
    private QMUICommonListItemView itemImageLabel;
    private String heightStr;
    private String weightStr;
    private String albleIdArr;

    @Override
    protected Context getNowContext() {
        return EditActivity.this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_player_redact;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.admin_color), 0);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        redactRecycler = findViewById(R.id.redact_recycler);
        headImg = findViewById(R.id.head_img);
        redactNameText = findViewById(R.id.redact_nameText);
        redactSexText = findViewById(R.id.redact_sextext);
        mRlChangeNameLayout = findViewById(R.id.redact_name);

        //个性签名
        signTv = findViewById(R.id.redact_sign_tv);
        signRl = findViewById(R.id.redact_sign);
        signRl.setOnClickListener(this);


        itemHeight = groupListView.createItemView(getString(R.string.height));
        itemHeight.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemHeight.setId(R.id.auth_user_height);
        itemHeight.getDetailTextView().setVisibility(View.VISIBLE);
        itemHeight.getDetailTextView().setHint(R.string.edit_input_height);


        itemWeight = groupListView.createItemView(getString(R.string.weight));
        itemWeight.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemWeight.setId(R.id.auth_user_weight);
        itemWeight.getDetailTextView().setVisibility(View.VISIBLE);
        itemWeight.getDetailTextView().setHint(R.string.edit_input_weight);

        itemConstellation = groupListView.createItemView(getString(R.string.constellation));
        itemConstellation.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemConstellation.setId(R.id.auth_user_constellation);
        itemConstellation.getDetailTextView().setVisibility(View.VISIBLE);
        itemConstellation.getDetailTextView().setHint(R.string.edit_auth_set_constellation);

        itemIntroduce = groupListView.createItemView(getString(R.string.edit_introduce));
        itemIntroduce.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemIntroduce.setId(R.id.auth_introduce);
        itemIntroduce.getDetailTextView().setVisibility(View.VISIBLE);
        itemIntroduce.getDetailTextView().setHint(R.string.edit_auth_set_introduce);

        itemImageLabel = groupListView.createItemView(getString(R.string.edit_auth_image_label));
        itemImageLabel.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        itemImageLabel.setId(R.id.auth_image_label);
        itemImageLabel.getDetailTextView().setVisibility(View.VISIBLE);
        itemImageLabel.getDetailTextView().setHint(R.string.edit_auth_set_image_label);


        QMUIGroupListView.Section section = QMUIGroupListView.newSection(this)
                .addItemView(itemHeight, this)
                .addItemView(itemWeight, this)
                .addItemView(itemConstellation, this)
                .addItemView(itemIntroduce, this)
                .addItemView(itemImageLabel, this);

        section.addTo(groupListView);

        initAdapter();
    }

    @Override
    protected void initSet() {
        getTopBar().setBackgroundResource(R.color.white);
        getTopBar().setTitle(getString(R.string.edit_info)).setTextColor(getNowContext().getResources().getColor(R.color.black));
        Button button = getTopBar().addRightTextButton(getString(R.string.save), R.id.redact_savebtn);
        button.setTextColor(getNowContext().getResources().getColor(R.color.black));
        button.setOnClickListener(this);
        //getTopBar().addLeftImageButton(R.drawable.icon_back_black, R.id.redact_backbtn).setOnClickListener(this);
        setOnclickListener(R.id.head_img, R.id.redact_name, R.id.redact_sex);

    }

    @Override
    protected boolean hasTopBar() {
        return true;
    }

    /**
     * 重置适配器
     */
    private void initAdapter() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        redactRecycler.setLayoutManager(manager);

        redactAdapter = new GridImageAdapter(this, onAddPicClickListener);
        redactAdapter.setList(selectList);
        redactAdapter.setSelectMax(6);
        redactRecycler.setAdapter(redactAdapter);
    }

    @Override
    protected void initData() {
        uId = SaveData.getInstance().getId();
        uToken = SaveData.getInstance().getToken();

        cuckooQiniuFileUploadUtils = new CuckooQiniuFileUploadUtils();
        //初始化数据
        requestUserData();
    }

    @Override
    protected void initPlayerDisplayData() {
    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {

            doSelectImage(1);

//            PictureSelector.create(EditActivity.this)
//                    .openGallery(selectType)
//                    .maxSelectNum(6)
//                    .previewVideo(true)
//                    .recordVideoSecond(60)
//                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }
    };


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.redact_backbtn:
                finish();
                break;
            case R.id.redact_savebtn:
                sign = signTv.getText().toString().trim();
                uploadPageImg();
                break;
            case R.id.head_img:
                doChangeHead();
                break;
            case R.id.redact_name:
                showDialogEdNicknameText();
                break;
            case R.id.redact_sex:
                showDialogSex();
                break;
            case R.id.redact_sign:
                showDialogSignEdText();
                break;
            case R.id.auth_user_weight:
                onNumberWeightPicker();
                break;
            case R.id.auth_user_height:
                onNumberHeightPicker();
                break;
            case R.id.auth_user_nickname:
                clickEditUserNickname();
                break;
            case R.id.auth_bind_phone:
                clickBindPhone();
                break;
            case R.id.auth_user_constellation:
                onConstellationPicker();
                break;
            case R.id.auth_self_label:
                clickEditSelfSign();
                break;
            case R.id.auth_introduce:
                clickEditSelfIntroduce();
                break;
            case R.id.auth_image_label:
                clickSelectLabel();
                break;
            default:
                break;
        }
    }
    //////////////////////////////////////业务处理//////////////////////////////////////////////////

    /**
     * 请求用户资料
     *
     * @return
     */
    private void requestUserData() {

        Api.getUserDataAtCompile(uId, uToken, new JsonCallback() {
            @Override
            public Context getContextToJson() {
                return getNowContext();
            }

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestUser userJ = JsonRequestUser.getJsonObj(s);
                if (userJ.getCode() == 1) {
                    UserData userData = userJ.getData();
                    log(userData.toString());
                    //显示数据
                    Utils.loadHttpImg(EditActivity.this, Utils.getCompleteImgUrl(userData.getAvatar()), headImg);

                    redactNameText.setText(userData.getUser_nickname());
                    if (userData.getSex() == 0) {
                        isAlterSex = true;
                    }
                    if (!TextUtils.isEmpty(userData.getSign())) {
                        signTv.setText(userData.getSign());
                    } else {
                        signTv.setText("还未设置个性签名");
                    }

                    redactSexText.setText(Utils.getSex(userData.getSex()));
                    //保存数据
                    sex = userData.getSex();
                    imgLoader.clear();
                    imgLoader.addAll(userData.getImg());

                    if (ApiUtils.isTrueUrl(userData.getAvatar())) {
                        ApiUtils.getUrlBitmapToSD(userData.getAvatar(), "headImage");
                    }

                    isAddPlus = false;

                    if (StringUtils.toInt(userData.getIs_change_name()) != 1) {
                        mRlChangeNameLayout.setEnabled(false);
                    }


                    itemHeight.setDetailText(userData.getHeight() + "cm");
                    itemWeight.setDetailText(userData.getWeight() + "kg");
                    itemConstellation.setDetailText(userData.getConstellation());
                    itemImageLabel.setDetailText(userData.getImage_label());
                    itemIntroduce.setDetailText(userData.getIntroduce());


                    selectList.clear();
                    //网络刷新&&请求图片
                    for (UserImgModel url : userJ.getData().getImg()) {
                        LocalMedia localMedia = new LocalMedia();
                        localMedia.setPath(url.getImg());
                        selectList.add(localMedia);
                    }

                    redactAdapter.setNewList(selectList);
                    redactAdapter.notifyDataSetChanged();
                } else {
                    showToastMsg(userJ.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                finishNow();//数据请求异常,退出当前页面
            }
        });
    }

    private List<String> fileUrlList = new ArrayList<>();

    //上传封面图
    private void uploadPageImg() {

        cuckooQiniuFileUploadUtils.uploadFileLocalMedia(LiveConstant.AVATAR_DIR, redactAdapter.getList(), new CuckooQiniuFileUploadUtils.CuckooQiniuFileUploadCallback() {
            @Override
            public void onUploadFileSuccess(List<String> list) {
                fileUrlList.clear();
                fileUrlList.addAll(list);

                if (headImgFile != null && headImgFile.exists()) {
                    List<File> headFile = new ArrayList<>();
                    headFile.add(headImgFile);
                    //上传头像
                    cuckooQiniuFileUploadUtils.uploadFile(LiveConstant.AVATAR_DIR, headFile, new CuckooQiniuFileUploadUtils.CuckooQiniuFileUploadCallback() {
                        @Override
                        public void onUploadFileSuccess(List<String> avatar) {
                            saveUserData(fileUrlList, avatar.get(0));
                        }
                    });
                } else {
                    saveUserData(fileUrlList, null);
                }
            }
        });
    }

    /**
     * 提交保存修改信息
     */
    private void saveUserData(List<String> pageImgList, String avatar) {

        String height = itemHeight.getDetailText().toString();
        String weight = itemWeight.getDetailText().toString();

        if (height.contains("cm")) {
            heightStr = height.substring(0, height.length() - 2);
        } else {
            heightStr = height;
        }

        if (weight.contains("kg")) {
            weightStr = weight.substring(0, weight.length() - 2);

        } else {
            weightStr = weight;
        }

        showLoadingDialog(getString(R.string.loading_upload_data));
        Api.saveUserDataAtCompile(
                heightStr,
                weightStr,
                itemConstellation.getDetailText().toString(),
                itemIntroduce.getDetailText().toString(),
                itemImageLabel.getDetailText().toString(),
                "",
                uId,
                uToken,
                redactNameText.getText().toString(),
                avatar,
                sex,
                pageImgList,
                sign,
                new JsonCallback() {
                    @Override
                    public Context getContextToJson() {
                        return getNowContext();
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        hideLoadingDialog();
                        tipD = new QMUITipDialog.Builder(getNowContext())
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_SUCCESS)
                                .setTipWord(getString(R.string.submit_success))
                                .create();
                        showThenDialog(tipD);
                        //重新请求数据
                        requestUserData();
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        hideLoadingDialog();
                    }
                });
    }

    /**
     * 执行选择图片添加操作
     */
    private void doSelectImage(int type) {
        selectImageType = type;

        int maxCount;
        if (selectImageType == 0) {
            maxCount = 1;
        } else {
            maxCount = 5;
        }

        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(maxCount)
                .enableCrop(true)// 是否裁剪 true or false
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }


    private static final int REQUEST_CODE = 1;
    public static final int RESULT_SELF_INTRODUCE_CODE = 3;
    public static final int RESULT_SELF_SIGN_CODE = 2;

    public void onNumberHeightPicker() {
        NumberPicker picker = new NumberPicker(this);
        picker.setWidth(picker.getScreenWidthPixels());
        picker.setCycleDisable(false);
        picker.setDividerVisible(false);
        picker.setOffset(2);//偏移量
        picker.setRange(145, 200, 1);//数字范围
        picker.setSelectedItem(172);
        picker.setLabel("厘米");
        picker.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                itemHeight.setDetailText(item.toString() + "cm");
            }
        });
        picker.show();
    }

    public void onNumberWeightPicker() {
        NumberPicker picker = new NumberPicker(this);
        picker.setWidth(picker.getScreenWidthPixels());
        picker.setCycleDisable(false);
        picker.setDividerVisible(false);
        picker.setOffset(2);//偏移量
        picker.setRange(40, 100, 1);//数字范围
        picker.setSelectedItem(172);
        picker.setLabel("公斤");
        picker.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
            @Override
            public void onNumberPicked(int index, Number item) {
                itemWeight.setDetailText(item.toString() + "kg");
            }
        });
        picker.show();
    }

    private void clickBindPhone() {

        Intent intent = new Intent(this, CuckooAuthPhoneActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void clickSelectLabel() {

        Intent intent = new Intent(this, CuckooSelectLabelActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void clickEditSelfIntroduce() {
        Intent intent = new Intent(this, CuckooAuthEditBodyActivity.class);
        intent.putExtra("RESULT_CODE", RESULT_SELF_INTRODUCE_CODE);
        intent.putExtra(CuckooAuthEditBodyActivity.TITLE_LABEL, getString(R.string.edit_auth_self_introduce));
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void clickEditSelfSign() {

        Intent intent = new Intent(this, CuckooAuthEditBodyActivity.class);
        intent.putExtra("RESULT_CODE", RESULT_SELF_SIGN_CODE);
        intent.putExtra(CuckooAuthEditBodyActivity.TITLE_LABEL, getString(R.string.edit_auth_self_sign));
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void clickEditUserNickname() {
        Intent intent = new Intent(this, CuckooAuthUserNicknameActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }


    public void onConstellationPicker() {
        boolean isChinese = Locale.getDefault().getDisplayLanguage().contains("中文");
        OptionPicker picker = new OptionPicker(this,
                isChinese ? new String[]{
                        "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座",
                        "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"
                } : new String[]{
                        "Aquarius", "Pisces", "Aries", "Taurus", "Gemini", "Cancer",
                        "Leo", "Virgo", "Libra", "Scorpio", "Sagittarius", "Capricorn"
                });

        picker.setCycleDisable(false);//不禁用循环
        picker.setTopBackgroundColor(0xFFEEEEEE);
        picker.setTopHeight(30);
        picker.setTopLineColor(getResources().getColor(R.color.line_color));
        picker.setTopLineHeight(1);
        picker.setTitleText(isChinese ? "请选择" : "Please pick");
        picker.setTitleTextColor(getResources().getColor(R.color.picker_color));
        picker.setTitleTextSize(12);
        picker.setCancelTextColor(getResources().getColor(R.color.picker_color));
        picker.setCancelTextSize(13);
        picker.setSubmitTextColor(getResources().getColor(R.color.picker_color));
        picker.setSubmitTextSize(13);
        picker.setTextColor(getResources().getColor(R.color.picker_color), 0xFF999999);
        WheelView.DividerConfig config = new WheelView.DividerConfig();
        config.setColor(getResources().getColor(R.color.picker_color));//线颜色
        config.setAlpha(140);//线透明度
        config.setRatio((float) (1.0 / 8.0));//线比率
        picker.setDividerConfig(config);
        picker.setBackgroundColor(0xFFEEEEEE);
        //picker.setSelectedItem(isChinese ? "处女座" : "Virgo");
        picker.setSelectedIndex(7);
        picker.setCanceledOnTouchOutside(true);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                itemConstellation.setDetailText(item);
            }
        });
        picker.show();
    }


    public static final int RESULT_NICKNAME_CODE = 1;
    public static final int RESULT_SELF_LABEL = 4;
    public static final int RESULT_PHONE = 5;
    public static final String USER_NICKNAME = "USER_NICKNAME";
    public static final String USER_BODY = "USER_BODY";
    public static final String USER_LABEL = "USER_LABEL";
    public static final String USER_PHONE = "USER_PHONE";

    public static final String STATUS = "STATUS";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_SELF_INTRODUCE_CODE) {
                String name = data.getStringExtra(USER_BODY);
                itemIntroduce.setDetailText(name);
            }

            if (resultCode == RESULT_SELF_LABEL) {
                String lableName = data.getStringExtra(USER_LABEL);
                itemImageLabel.setDetailText(lableName);
            }
        }

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:

                    if (selectImageType == 0) {
                        selectImageType = 0;
                        headImgFile = new File(PictureSelector.obtainMultipleResult(data).get(0).getCutPath());
                        headImg.setImageBitmap(ImageUtil.getBitmapByPath(headImgFile.getAbsolutePath()));
                    } else {
                        // 图片选择结果回调
                        selectList = PictureSelector.obtainMultipleResult(data);
                        //选择成功
                        filesByAll.clear();
                        for (LocalMedia item : selectList) {
                            filesByAll.add(new File(item.getPath()));
                        }

                        redactAdapter.setList(selectList);
                        redactAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 头像是否可以修改
     *
     * @return true-false
     */
    private boolean isChangeHead() {
        return true;
    }

    /**
     * 换头像
     */
    private void doChangeHead() {
        if (isChangeHead()) {
            doSelectImage(0);
        } else {
            showDialogMsg(getString(R.string.head_img));
        }
    }

    /**
     * 更换性别
     */
    private void showDialogSex() {
        if (isAlterSex) {
            showDialogChecked();
        } else {
            showToastMsg(getString(R.string.sex_not_change));
        }
    }

    /**
     * 显示一个消息提示框
     */
    private void showDialogMsg(String string) {
        new QMUIDialog.MessageDialogBuilder(getNowContext())
                .setTitle(string + getString(R.string.not_change))
                .setMessage("当前无法修改" + string + "," + string + "一个月只能修改一次!")
                .addAction(0, "知道了", QMUIDialogAction.ACTION_PROP_NEGATIVE, new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 显示一个文本输入框
     */
    private void showDialogEdNicknameText() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getNowContext());
        builder.setTitle(getString(R.string.edit_nickname))
                .setPlaceholder("昵称每30天仅可以修改一次")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("不改了", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确认修改", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 6) {
                            showToastMsg("昵称长度超过最大6个字符！");
                            return;
                        }
                        if (text != null && text.length() > 0) {
                            //执行修改操作
                            redactNameText.setText(text);
                            dialog.dismiss();
                        } else {
                            //未输入
                            showToastMsg("昵称不可以为空!");
                        }
                    }
                }).show();
    }

    /**
     * 显示一个文本输入框
     */
    private void showDialogSignEdText() {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getNowContext());
        builder.setTitle(getString(R.string.edit_sign))
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("不改了", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确认修改", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 15) {
                            showToastMsg("签名长度超过最大15个字符！");
                            return;
                        }
                        if (text != null && text.length() > 0) {
                            //执行修改操作
                            signTv.setText(text);
                            dialog.dismiss();
                        } else {
                            //未输入
                            showToastMsg("签名不可以为空!");
                        }
                    }
                }).show();
    }

    /**
     * 显示一个选择框
     */
    private void showDialogChecked() {
        final String[] items = new String[]{"男", "女"};
        new QMUIDialog.MenuDialogBuilder(getNowContext())
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        redactSexText.setText(items[which]);
                        sex = which + 1;
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
