package com.matrixdeveloper.battle.club.tournaments.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.matrixdeveloper.battle.club.tournaments.R;
import com.matrixdeveloper.battle.club.tournaments.config.config;
import com.matrixdeveloper.battle.club.tournaments.data.Play;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> {

    private final Context ctx;

    private final List<Play> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView title;

        final CardView cardView;
        final TextView fee;
        final ImageView img;
        final Button joinBtn;
        final TextView map;
        final TextView perkill;
        final RelativeLayout privateTextArea;
        final TextView prize;
        final TextView sponsorText;
        final RelativeLayout sponsorTextArea;
        final TextView timedate;
        final ImageView topImage;
        final TextView type;
        final TextView version;
        final Button watchBtn;

        MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.mainCard);
            img = (ImageView) view.findViewById(R.id.img);
            title = (TextView) view.findViewById(R.id.title);
            timedate = (TextView) view.findViewById(R.id.timedate);
            prize = (TextView) view.findViewById(R.id.winPrize);
            perkill = (TextView) view.findViewById(R.id.perKill);
            fee = (TextView) view.findViewById(R.id.entryFee);
            watchBtn = (Button) view.findViewById(R.id.watchMatchButton);
            joinBtn = (Button) view.findViewById(R.id.joinedButton);
            sponsorTextArea = (RelativeLayout) view.findViewById(R.id.sponsorTextArea);
            sponsorText = (TextView) view.findViewById(R.id.sponsorText);
            type = (TextView) view.findViewById(R.id.matchType);
            version = (TextView) view.findViewById(R.id.matchVersion);
            map = (TextView) view.findViewById(R.id.matchMap);
            topImage = (ImageView) view.findViewById(R.id.mainTopBanner);
            privateTextArea = (RelativeLayout) view.findViewById(R.id.privateTextArea);
        }
    }


    public ResultAdapter(Context context, List<Play> moviesList) {
        ctx = context;
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Play play = moviesList.get(position);
        holder.title.setText(play.getTitle());
        holder.title.setText("Match#" + play.getTitle());
        TextView textView = holder.timedate;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Time: ");

        //Input date in String format
        String input = play.getTimeDate();
        //Date/time pattern of input date
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Date/time pattern of desired output date
        DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat outputtimeformat = new SimpleDateFormat("hh:mm aa");
        Date date;
        String output = null;
        try {
            //Conversion of input String to date
            date = df.parse(input);
            //old date format to new date format
            output = outputformat.format(date) + " at " + outputtimeformat.format(date);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        stringBuilder.append(output);
        textView.setText(stringBuilder.toString());
        holder.prize.setText(play.getWinPrize());
        holder.perkill.setText(play.getPerKill());
        String matchType = play.getMatchType();
        if (matchType.equals("Free")) {
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
        } else if (matchType.equals("Sponsored")) {
            textView = holder.sponsorText;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Sponsored by ");
            stringBuilder.append(play.getSponsoredby());
            textView.setText(stringBuilder.toString());
            holder.sponsorTextArea.setVisibility(View.VISIBLE);
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
        } else if (matchType.equals("Giveaway")) {
            textView = holder.sponsorText;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Giveaway by ");
            stringBuilder.append(play.getSponsoredby());
            textView.setText(stringBuilder.toString());
            holder.sponsorTextArea.setVisibility(View.VISIBLE);
            holder.fee.setText("FREE");
            holder.fee.setTextColor(Color.parseColor("#1E7E34"));
        } else {
            holder.fee.setText(play.getEntryFee());
        }
        holder.type.setText(play.getType());
        holder.version.setText(play.getVersion());
        holder.map.setText(play.getMap());
        if (play.getImgURL().contains("png") || play.getImgURL().contains("jpg")) {
            String img = config.mainimg + play.getImgURL();
            Glide.with(ctx).load(img).placeholder(R.drawable.circlesmall).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.img);
        }
        if (play.getTopImage().contains("png") || play.getTopImage().contains("jpg")) {
            holder.topImage.setVisibility(View.VISIBLE);
            String img = config.mainimg + play.getTopImage();
            Glide.with(ctx).load(img).placeholder(R.drawable.wp).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.topImage);
        }
        if (Integer.parseInt(play.getJoin_status()) == 0) {
            holder.joinBtn.setText("Not Join");
        } else if (Integer.parseInt(play.getJoin_status()) == 1) {
            holder.joinBtn.setText("Joinned");
        }
        holder.watchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager pm = ctx.getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage("com.google.android.youtube");
                try {
                    if (intent != null) {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(config.youtubechannel));
                        ctx.startActivity(intent);
                    } else {
                        // Bring user to the market or let them choose an app?
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse("market://details?id=" + "com.google.android.youtube"));
                        if (intent != null) {
                            ctx.startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
