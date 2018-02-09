package com.lht.cloudjob.fragment;

import android.content.Context;
import android.widget.ImageView;

import com.lht.cloudjob.R;
import com.lht.cloudjob.activity.BaseActivity;
import com.lht.cloudjob.interfaces.bars.OnToggleListener;
import com.lht.cloudjob.util.debug.DLog;
import com.squareup.picasso.Picasso;

/**
 * <p><b>Package</b> com.lht.cloudjob.fragment
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> AvatarBarFragment
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/8/9.
 */
public abstract class AvatarBarFragment extends BaseFragment implements OnToggleListener {

    private OnFragmentInteractionListener mListener;

    @Override
    public void onOpenMore() {
        getFragmentInteractionListener().onToolbarToggle(true);
    }

    @Override
    public void onCloseMore() {
        getFragmentInteractionListener().onToolbarToggle(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onToolbarToggle(boolean isToggle);

        String onPreAvatarLoad();

        void onSearchClick();
    }

    public OnFragmentInteractionListener getFragmentInteractionListener() {
        return mListener;
    }

    public void setmListener(OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
    }

    public void loadAvatar(String path) {
        DLog.d(getClass(), "check avatar:" + path);
        if (getAvatarView() == null) {
            return;
        }
        Picasso.with(getContext()).load(path)
                .placeholder(R.drawable.v1010_drawable_avatar_default)
                .error(R.drawable.v1010_drawable_avatar_default)
                .diskCache(BaseActivity.getLocalImageCache()).into(getAvatarView());
    }


    protected abstract ImageView getAvatarView();
}
