package altcoin.br.vcash.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import altcoin.br.vcash.R;
import altcoin.br.vcash.model.Link;
import altcoin.br.vcash.utils.Utils;

public class AdapterLinks extends BaseAdapter {
    private List<Link> links;
    private Context context;

    public AdapterLinks(Context context, List<Link> links) {
        this.context = context;
        this.links = links;
    }

    @Override
    public int getCount() {
        return links.size();
    }

    @Override
    public Object getItem(int i) {
        return links.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = li.inflate(R.layout.row_links, null);

        TextView tvLinkLabel = (TextView) v.findViewById(R.id.tvLinkLabel);
        TextView tvLinkUrl = (TextView) v.findViewById(R.id.tvLinkUrl);

        tvLinkLabel.setText(links.get(position).getLabel());
        tvLinkUrl.setText(links.get(position).getUrl());

        Utils.textViewLink(tvLinkUrl, links.get(position).getUrl());

        return v;
    }
}
