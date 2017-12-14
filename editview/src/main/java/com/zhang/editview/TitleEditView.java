package com.zhang.editview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhang.editview.ex.ImportEmptyException;
import com.zhang.editview.ex.TextLengthException;
import com.zhang.editview.span.NoUnderlineRedSpan;
import com.zhang.editview.spf.InputSpf;
import com.zhang.editview.spf.SpfHelp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




/**
 * Created by zhang on 2017/8/28.
 * 统一的单行和多行的编辑显示框
 */

public class TitleEditView extends LinearLayout {
    private static final String TAG = "TitleEditView";
    private boolean isSingle = true; //默认是单行
    private boolean isEdit = true; //默认是编辑状态
    private boolean isClick = false;
    private boolean isImport = false; //默认非必填
    private boolean isSaveInput = false;
    private int textColor = Color.BLACK;
    private String saveTag;
    private String title; //标题
    private String hint; //提示信息
    private String text; //内容文本 注意要使用app:text
    int inputType = EditorInfo.TYPE_CLASS_TEXT;//输入的类型 2.表示数字和小数点
    private int maxTextLength; //默认200


    private View view;
    private String separator = ";";

    ViewHolder holder;
    private Context mContext;

    public TitleEditView(Context context) {
        super(context);
        init(null, 0);
    }

    public TitleEditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TitleEditView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        mContext = this.getContext();
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TitleEditView, defStyle, 0);

        isImport = a.getBoolean(
                R.styleable.TitleEditView_isImport, false);
        isEdit = a.getBoolean(
                R.styleable.TitleEditView_isEdit, true);

        isSingle = a.getBoolean(
                R.styleable.TitleEditView_isSingle, isEdit);
        title = a.getString(
                R.styleable.TitleEditView_title);
        hint = a.getString(
                R.styleable.TitleEditView_hint);
        text = a.getString(
                R.styleable.TitleEditView_text);
        inputType = a.getInteger(
                R.styleable.TitleEditView_inputType, EditorInfo.TYPE_CLASS_TEXT);
        maxTextLength = a.getInteger(
                R.styleable.TitleEditView_tTextMaxLength, 200);
        isClick = a.getBoolean(R.styleable.TitleEditView_isClick, false);
        textColor = a.getColor(R.styleable.TitleEditView_tTextColor, Color.BLACK);
        a.recycle();

        inflateView();
        initView();


    }

    /*
    初始化值
     */
    private void initView() {
        if (holder == null) {
            return;
        }
        if (isSingle) {
            holder.etContent.setMaxLines(1);
        }
        if (isImport) {
            holder.tvImport.setVisibility(VISIBLE);
        } else {
            holder.tvImport.setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(title)) { //有標題的时候显示标题内容
            if (title.contains(";") || title.contains("：")) {
                holder.tvTitle.setText(title);
            } else {
                holder.tvTitle.setText(title + "：");
            }
        } else {
            holder.tvTitle.setVisibility(GONE);
        }
        holder.etContent.setHint(hint);
        holder.etContent.setText(text);
        holder.etContent.setTextColor(textColor); //设置内容的颜色
        if (isSaveInput()) {
            setInputAdapter();
        }
        if (isSingle()) {
            holder.etContent.setInputType(getInputType());
        } else {
        }

        if (holder.etContent instanceof EditText) {
            holder.tvWordsIndicator.setText("0" + "/" + maxTextLength);
            holder.etContent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                NoUnderlineRedSpan noUnderlineRedSpan = new NoUnderlineRedSpan();

                @Override
                public void afterTextChanged(Editable s) {
                    String count = s.length() + "";
                    if (s.length() > maxTextLength) {
                        s.setSpan(noUnderlineRedSpan, maxTextLength, s.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        count = String.format(mContext.getString(R.string.red_format), s.length() + "");
                    } else {
                        s.removeSpan(noUnderlineRedSpan);
                    }
                    holder.tvWordsIndicator.setText(Html.fromHtml(count + "/" + maxTextLength));
                }
            });
        }

    }

    /**
     * 设置自动输入的适配器
     */
    private void setInputAdapter() {
        ArrayAdapter<String> inputAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, getFromSpf());
        if (holder.etContent instanceof AutoCompleteTextView) {
            ((AutoCompleteTextView) holder.etContent).setAdapter(inputAdapter);
        }
    }


    /**
     * 加载布局
     */
    private void inflateView() {

        if (isClick) { //判断是否是点击形式
            view = inflate(this.getContext(), R.layout.view_title_click_single, this);
        } else if (isEdit) { //編輯狀態
            if (isSingle) {
                view = inflate(this.getContext(), R.layout.view_title_edit_single_edit, this);
            } else {
                view = inflate(this.getContext(), R.layout.view_title_edit_multi_edit, this);
            }
        } else { //显示状态
//            if (isSingle) {
            view = inflate(this.getContext(), R.layout.view_title_edit_single, this);
//            } else {
//                view = inflate(this.getContext(), R.layout.view_title_edit_multi, this);
//            }
        }
        holder = new ViewHolder(view);

    }


    static class ViewHolder {
        TextView tvImport;
        TextView tvTitle;
        TextView etContent;
        TextView tvWordsIndicator;

        ViewHolder(View view) {
            tvImport = view.findViewById(R.id.tv_t_import);
            tvTitle = view.findViewById(R.id.tv_t_title);
            etContent = view.findViewById(R.id.et_t_content);
            tvWordsIndicator = view.findViewById(R.id.tv_words_indicator);
        }
    }

    public void setText(String text) {
        if (TextUtils.isEmpty(text)) { //非null
            text = "";
        }
        this.text = text;
        holder.etContent.setText(this.text);
        holder.etContent.requestLayout();

    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
        this.removeAllViews();
        inflateView();
        initView();
    }

    public int getInputType() {
        if (inputType == 2) {
            inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
        }
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
        if (holder != null) {
            holder.etContent.setInputType(inputType);
        }
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
        this.removeAllViews();
        inflateView();
        initView();
    }

    public boolean isImport() {
        return isImport;
    }

    public void setImport(boolean anImport) {
        isImport = anImport;
        holder.tvImport.setVisibility(isImport ? VISIBLE : GONE);
    }

    public String getTitle() {
        title = holder.tvTitle.getText().toString();
        String titleName = title.replace(":", "").replace("：", "");
        return titleName;
    }

    public void setTitle(String title) {
        this.title = title;
        if (!TextUtils.isEmpty(title)) { //有標題的时候显示标题内容
            if (title.contains(";") || title.contains("：")) {
                holder.tvTitle.setText(title);
            } else {
                holder.tvTitle.setText(title + "：");
            }
        } else {
            holder.tvTitle.setText("");
            holder.tvTitle.setVisibility(GONE);
        }
    }

    public void setSaveInput(boolean saveInput) {
        isSaveInput = saveInput;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }


    public boolean isSaveInput() {
        return isSaveInput;
    }

    /**
     * 那个页面进行的保存
     *
     * @param activity  页面的标识
     * @param saveInput
     */
    public void setSaveInput(String activity, boolean saveInput) {
        if (activity == null) {
            return;
        }
        saveTag = activity;
        isSaveInput = saveInput;
        if (holder != null) {
            setInputAdapter();
        }
    }

    public void setSaveInput(Activity activity, boolean saveInput) {
        if (activity == null) {
            return;
        }
        saveTag = activity.getLocalClassName();
        isSaveInput = saveInput;
        if (holder != null) {
            setInputAdapter();
        }
    }

    public String getText()  {
        text = holder.etContent.getText().toString();
        if (TextUtils.isEmpty(text)) {
            if (isImport()) {
                String titleName = title.replace(":", "").replace("：", "");
                holder.etContent.setHintTextColor(getResources().getColor(R.color.red));
                holder.etContent.setHint(getContext().getString(R.string.please_input) + titleName);
                Log.w(TAG, getContext().getString(R.string.please_input) + titleName);
                holder.etContent.requestFocus();
                return text;
            }else{
                return text;
            }
        } else {

            if (isSaveInput()) {
                saveToSpf();
            }
            return text;
        }
    }


    public String getCheckedText() throws ImportEmptyException, TextLengthException {
        text = holder.etContent.getText().toString();
        if (TextUtils.isEmpty(text)) {
            if (isImport()) {
                String titleName = title.replace(":", "").replace("：", "");
                holder.etContent.setHintTextColor(getResources().getColor(R.color.red));
                holder.etContent.setHint(getContext().getString(R.string.please_input) + titleName);
                Log.w(TAG, getContext().getString(R.string.please_input) + titleName);
                holder.etContent.requestFocus();
                throw new ImportEmptyException();
            }else{
                return text;
            }
        } else {
            if (text.length() > maxTextLength) {
                throw new TextLengthException();
            }
            if (isSaveInput()) {
                saveToSpf();
            }
            return text;
        }
    }

    protected void saveToSpf() {
        String lastText = SpfHelp.getStringFormSpf(getContext(),InputSpf.FILE_INPUT, saveTag);
        if (getFromSpf().contains(text)) { //已有该条数据就不在进行保存
            return;
        }
        if (!TextUtils.isEmpty(lastText)) {
            String thisText = lastText + separator + text;
            SpfHelp.putString2Spf(getContext(),InputSpf.FILE_INPUT, saveTag, thisText);
        } else {
            SpfHelp.putString2Spf(getContext(),InputSpf.FILE_INPUT, saveTag, text);
        }
    }

    protected List<String> getFromSpf() {
        List<String> inputLists = new ArrayList<>();

        String text = SpfHelp.getStringFormSpf(getContext(),InputSpf.FILE_INPUT, saveTag);
        if (TextUtils.isEmpty(text)) {
            return inputLists;
        } else if (text.contains(separator)) {
            String[] textArr = text.split(separator);
            inputLists = Arrays.asList(textArr);
        } else {
            inputLists.add(text);
        }
        return inputLists;
    }

    public TextView getEtView() {
        if (holder == null) {
            return null;
        }
        return holder.etContent;
    }

    public boolean isClick() {
        return isClick;
    }



    public int getMaxTextLength() {
        return maxTextLength;
    }
}
