package com.sharathp.blabber.views.adapters.viewholders;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sharathp.blabber.R;
import com.sharathp.blabber.models.ITweetWithUser;
import com.yahoo.squidb.data.AbstractModel;
import com.yahoo.squidb.recyclerview.SquidViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoadingItemHolder<T extends AbstractModel & ITweetWithUser> extends SquidViewHolder<T> {

    @BindView(R.id.pb_loading_progress_bar)
    ProgressBar mLoadingProgressBar;

    @BindView(R.id.tv_article_end_message)
    TextView mEndOfArticlesTextView;

    public LoadingItemHolder(final View itemView, final T model) {
        super(itemView, model);
        ButterKnife.bind(this, itemView);
    }

    public void showEndOfFeedReached() {
        mLoadingProgressBar.setVisibility(View.GONE);
        mEndOfArticlesTextView.setVisibility(View.VISIBLE);
    }

    public void hideEndOfFeedReached() {
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mEndOfArticlesTextView.setVisibility(View.GONE);
    }
}
