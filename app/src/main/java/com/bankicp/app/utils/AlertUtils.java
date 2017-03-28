package com.bankicp.app.utils;

import java.util.ArrayList;
import java.util.List;

import com.bankicp.app.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public final class AlertUtils {
    /***
     * 登陆进度进度条
     */
    public static Dialog showLoading(Context context) {
        Dialog loadingdialog = new Dialog(context, R.style.loadingStyle);
        loadingdialog.setContentView(R.layout.loadingdig);
        // loadingdialog.findViewById(R.id.dig_msg);
        Window dialogWindow = loadingdialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        // WindowManager m = getWindowManager();
        // Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        // WindowManager.LayoutParams p = dialogWindow.getAttributes(); //
        // // 获取对话框当前的参数值
        // p.height = 200; // 高度设置为屏幕的200
        // p.width = (int) (d.getWidth() * 0.85); // 宽度设置为屏幕的0.85
        // dialogWindow.setAttributes(p);
        loadingdialog.setCanceledOnTouchOutside(false);
        loadingdialog.setCancelable(true);
        // loadingdialog.show();
        return loadingdialog;
    }

    public interface OnEditListener {
        void onTextChanged(String str);

        void onEditFinish(String str);
    }

    public interface OnSearchListener extends OnEditListener {
        void onClickType(View v);

    }

    public static Dialog showEdit(final Context context,
                                  final OnEditListener onEditListener) {
        return showEdit(context, null, null, onEditListener, 0);
    }

    public static Dialog showEdit(final Context context, String text,
                                  final OnEditListener onEditListener) {
        return showEdit(context, text, null, onEditListener, 0);
    }

    public static Dialog showEdit(final Context context, String text,
                                  String hint, final OnEditListener onEditListener) {
        return showEdit(context, text, hint, onEditListener, 0);
    }

    @SuppressLint("InflateParams")
    public static Dialog showEdit(final Context context, String text,
                                  String hint, final OnEditListener onEditListener, int type) {
        final Dialog dlg = new Dialog(context, R.style.theme_edit);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dig_btm_edit, null);

        final EditText editText = (EditText) layout.findViewById(R.id.edit);
        if (type == 1) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        }
        final Button button = (Button) layout.findViewById(R.id.btn_ok);
        if (!TextUtils.isEmpty(text)) {
            editText.setText(text);
            Selection.setSelection(editText.getText(), editText.length());
        }
        if (TextUtils.isEmpty(text) && !TextUtils.isEmpty(hint)) {
            editText.setHint(hint);
        }

        editText.addTextChangedListener(new TextWatcher() {
            String input = "";

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                input = arg0.toString();
                if (onEditListener != null) {
                    onEditListener.onTextChanged(input);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

                // if(onEditListener !=null){
                // onEditListener.onTextChanged(input);
                // }
            }
        });
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (onEditListener != null) {
                    onEditListener.onEditFinish(editText.getText().toString());
                }
                dlg.dismiss();
            }
        });
        Window dialogWindow = dlg.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();// 显示软键盘
        return dlg;
    }

    public static Dialog showSearch(final Context context, String text,
                                    String hint, final OnSearchListener searchListener) {
        final Dialog dlg = new Dialog(context, R.style.theme_search);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dig_search_edit, null);
        final EditText editText = (EditText) layout.findViewById(R.id.edit);
        final Button button = (Button) layout.findViewById(R.id.btn_ok);
        final Button typeBtn = (Button) layout.findViewById(R.id.btn_type);
        if (!TextUtils.isEmpty(text)) {
            editText.setText(text);
            button.setText("搜索");
            Selection.setSelection(editText.getText(), editText.length());
        }
        if (TextUtils.isEmpty(text)) {
            button.setText("取消");
            if (!TextUtils.isEmpty(hint))
                editText.setHint(hint);
        }

        editText.addTextChangedListener(new TextWatcher() {
            String input = "";

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                input = arg0.toString();
                if (input != null && input.length() == 0) {
                    button.setText("取消");
                } else {
                    button.setText("搜索");
                }
                if (searchListener != null) {
                    searchListener.onTextChanged(input);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {

                // if(onEditListener !=null){
                // onEditListener.onTextChanged(input);
                // }
            }
        });
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (searchListener != null) {
                    searchListener.onEditFinish(editText.getText().toString());
                }
                dlg.dismiss();
            }
        });
        typeBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (searchListener != null) {
                    searchListener.onClickType(v);
                }
            }
        });
        // WindowManager.LayoutParams lp = w.getAttributes();
        // lp.x = 0;
        // final int cMakeBottom = -1000;
        // lp.y = cMakeBottom;
        // lp.gravity = Gravity.TOP;
        // dlg.onWindowAttributesChanged(lp);
        //

        // Window dialogWindow = dlg.getWindow();
        // WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        // dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);

		/*
         * lp.x与lp.y表示相对于原始位置的偏移.
		 * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
		 * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
		 * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
		 * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
		 * 当参数值包含Gravity.CENTER_HORIZONTAL时
		 * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
		 * 当参数值包含Gravity.CENTER_VERTICAL时
		 * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
		 * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
		 * Gravity.CENTER_VERTICAL.
		 * 
		 * 本来setGravity的参数值为Gravity.LEFT | Gravity.TOP时对话框应出现在程序的左上角,但在
		 * 我手机上测试时发现距左边与上边都有一小段距离,而且垂直坐标把程序标题栏也计算在内了, Gravity.LEFT, Gravity.TOP,
		 * Gravity.BOTTOM与Gravity.RIGHT都是如此,据边界有一小段距离
		 */
        // lp.x = 0; // 新位置X坐标
        // lp.y = 0; // 新位置Y坐标
        // lp.width = LayoutParams.MATCH_PARENT; // 宽度
        // lp.height = 44; // 高度
        Window dialogWindow = dlg.getWindow();
        // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
        // dialog.onWindowAttributesChanged(lp);
        // dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.TOP);
        dialogWindow
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dlg.setCanceledOnTouchOutside(true);
        dlg.setContentView(layout);
        dlg.show();// 显示软键盘
        return dlg;
    }

    public interface OnAlertSelectId {
        void onClick(int whichButton);
    }

    private AlertUtils() {
    }

    public static Dialog showAlertBtn(final Context context, CharSequence str0,
                                      String str1, String str2, final OnClickListener clickListener,
                                      final OnClickListener cancelListener) {
        return showAlertBtn(context, null, false, "提示", str0, str1, str2,
                clickListener, cancelListener);

    }

    public static Dialog showAlertBtn(final Context context, boolean isCancel,
                                      CharSequence str0, final OnClickListener clickListener,
                                      final OnClickListener cancelListener) {
        return showAlertBtn(context, null, true, null, str0, null, null,
                clickListener, cancelListener);

    }

    /***
     * 显示对话框
     *
     * @param context        Context 上下文
     * @param contectView    View 自定义内容view
     * @param title
     * @param str1
     * @param str2
     * @param clickListener
     * @param cancelListener
     * @return
     */
    public static Dialog showAlertBtn(final Context context, View contectView,
                                      String title, String str1, String str2,
                                      final OnClickListener clickListener,
                                      final OnClickListener cancelListener) {
        return showAlertBtn(context, contectView, true, title, null, null,
                null, clickListener, cancelListener);

    }

    /**
     * 弹出一个二选对话框
     *
     * @param context
     * @param isCancel       是否点击外部取消
     * @param str0           //提示内容
     * @param str1           //左边按钮字
     * @param str2           //右边按钮字
     * @param clickListener  点击左边
     * @param cancelListener 点击右边
     * @return
     */
    @SuppressLint("InflateParams")
    public static Dialog showAlertBtn(final Context context, View contectView,
                                      boolean isCancel, CharSequence title, CharSequence content,
                                      String str1, String cancel, final OnClickListener clickListener,
                                      final OnClickListener cancelListener) {
        final Dialog dlg = new Dialog(context, R.style.theme_alert);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.uqload_alert_dig, null);
        final LinearLayout titile_lly = (LinearLayout) layout
                .findViewById(R.id.titile_lly);
        final LinearLayout contect_lly = (LinearLayout) layout
                .findViewById(R.id.contect_lly);
        final LinearLayout btm_lly = (LinearLayout) layout
                .findViewById(R.id.btm_lly);

        final TextView titleTv = (TextView) layout
                .findViewById(R.id.titile_text);// 内容
        final Button button1 = (Button) layout.findViewById(R.id.btn_ok);
        final Button button2 = (Button) layout.findViewById(R.id.btn_cancel);
        final TextView textTv = (TextView) layout
                .findViewById(R.id.contect_text);// 内容
        if (title == null) {
            titile_lly.setVisibility(View.GONE);
        }
        if (contectView != null) {
            contect_lly.addView(contectView);
            if (textTv != null)
                textTv.setVisibility(View.GONE);
        }
        if (clickListener == null && cancelListener == null) {
            btm_lly.setVisibility(View.GONE);
        }

        if (titleTv != null && !TextUtils.isEmpty(title)) {
            titleTv.setText(title);
        }
        if (textTv != null) {
            textTv.setText(content);
            textTv.setMovementMethod(new LinkMovementMethod());
        }
        if (str1 != null)
            button1.setText(str1);
        if (cancel != null)
            button2.setText(cancel);

        if (clickListener != null) {
            button1.setVisibility(View.VISIBLE);
            button1.setOnClickListener(clickListener);
        }
        if (cancelListener != null) {
            button2.setVisibility(View.VISIBLE);
            button2.setOnClickListener(cancelListener);
        }

        Window dialogWindow = dlg.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        dlg.setCanceledOnTouchOutside(isCancel);
        dlg.setContentView(layout);
        dlg.show();// 显示软键盘
        return dlg;
    }

    public static Dialog showAlert(final Context context, final String[] items,
                                   final OnAlertSelectId alertDo) {
        return showAlert(context, items, null, alertDo, null);
    }

    public static Dialog showAlert(final Context context, final String[] items,
                                   String cel, final OnAlertSelectId alertDo) {
        return showAlert(context, items, cel, alertDo, null);
    }

    /**
     * @param context Context.
     * @param title   The title of this AlertDialog can be null .
     * @param items   button name list.
     * @param alertDo methods call Id:Button + cancel_Button.
     * @param exit    Name can be null.It will be Red Color
     * @return A AlertDialog
     */
    @SuppressLint("InflateParams")
    public static Dialog showAlert(final Context context, final String[] items,
                                   String cel, final OnAlertSelectId alertDo,
                                   OnCancelListener cancelListener) {
        String cancel = TextUtils.isEmpty(cel) ? "取消" : cel;
        final Dialog dlg = new Dialog(context, R.style.theme_sheet);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.alert_dialog_menu_layout, null);
        final int cFullFillWidth = 10000;
        layout.setMinimumWidth(cFullFillWidth);
        final ListView list = (ListView) layout.findViewById(R.id.content_list);
        AlertAdapter adapter = new AlertAdapter(context, items, cancel);
        list.setAdapter(adapter);
        list.setDividerHeight(0);

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                System.out.println("position++++==>" + position);

                // if (!(title == null || title.equals("")) && position - 1 >=
                // 0) {
                // alertDo.onClick(position - 1);
                // dlg.dismiss();
                // list.requestFocus();
                // } else {
                // alertDo.onClick(position);
                // dlg.dismiss();
                // list.requestFocus();
                // }
                alertDo.onClick(position);
                dlg.dismiss();
                list.requestFocus();
            }
        });
        // set a large value put it in bottom
        Window w = dlg.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        final int cMakeBottom = -1000;
        lp.y = cMakeBottom;
        lp.gravity = Gravity.BOTTOM;
        dlg.onWindowAttributesChanged(lp);
        dlg.setCanceledOnTouchOutside(true);
        if (cancelListener != null)
            dlg.setOnCancelListener(cancelListener);

        dlg.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_MENU) {
                    dialog.dismiss();
                    dialog = null;
                    return true;
                }
                return false;
            }
        });

        dlg.setContentView(layout);

        dlg.show();
        return dlg;
    }

}

class AlertAdapter extends BaseAdapter {
    public static final int TYPE_BUTTON = 0;
    public static final int TYPE_BUTTOM = 1;
    public static final int TYPE_TOP = 2;
    public static final int TYPE_CANCEL = 3;
    // public static final int TYPE_TOP = 4;
    private List<String> items;
    private int[] types;
    private boolean isTitle = false;
    private Context context;

    public AlertAdapter(Context context, String[] items, String cancel) {
        if (items == null || items.length == 0) {
            this.items = new ArrayList<String>();
        } else {
            this.items = new ArrayList<String>();
            for (String string : items) {
                this.items.add(string);
            }

            // this.items = Util.stringsToList(items);
        }
        this.types = new int[this.items.size() + 3];
        this.context = context;
        if (this.items.size() > 1) {
            types[0] = TYPE_TOP;
            types[this.items.size() - 1] = TYPE_BUTTOM;
        }
        // if (title != null && !title.equals("")) {
        // types[0] = TYPE_TITLE;
        // this.isTitle = true;
        // this.items.add(0, title);
        // }
        //
        // if (exit != null && !exit.equals("")) {
        // // this.isExit = true;
        // types[this.items.size()] = TYPE_EXIT;
        // this.items.add(exit);
        // }

        if (cancel != null && !cancel.equals("")) {
            // this.isSpecial = true;
            types[this.items.size()] = TYPE_CANCEL;
            this.items.add(cancel);
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        if (position == 0 && isTitle) {
            return false;
        } else {
            return super.isEnabled(position);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String textString = (String) getItem(position);
        ViewHolder holder;
        int type = types[position];
        if (convertView == null
                || ((ViewHolder) convertView.getTag()).type != type) {
            holder = new ViewHolder();
            if (type == TYPE_CANCEL) {
                convertView = View.inflate(context,
                        R.layout.alert_dialog_menu_list_layout_cancel, null);
            } else if (type == TYPE_BUTTON) {
                convertView = View.inflate(context,
                        R.layout.alert_dialog_menu_list_layout, null);
            } else if (type == TYPE_BUTTOM) {
                convertView = View.inflate(context,
                        R.layout.alert_dialog_menu_list_layout_buttom, null);
            } else if (type == TYPE_TOP) {
                convertView = View.inflate(context,
                        R.layout.alert_dialog_menu_list_layout_top, null);
            }

            holder.text = (TextView) convertView.findViewById(R.id.popup_text);
            holder.type = type;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(textString);
        return convertView;
    }

    static class ViewHolder {
        TextView text;
        int type;
    }

}
