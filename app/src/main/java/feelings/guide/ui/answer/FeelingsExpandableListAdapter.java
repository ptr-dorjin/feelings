package feelings.guide.ui.answer;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import feelings.guide.R;

class FeelingsExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> feelingsGroups;
    private final Map<String, List<String>> mapFeelingsByGroup;

    FeelingsExpandableListAdapter(Context context, List<String> feelingsGroups,
                                  Map<String, List<String>> mapFeelingsByGroup) {
        this.context = context;
        this.feelingsGroups = feelingsGroups;
        this.mapFeelingsByGroup = mapFeelingsByGroup;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.feelings_group, parent, false);
        }

        TextView labelFeelingsGroup = convertView.findViewById(R.id.label_feelings_group);
        labelFeelingsGroup.setTypeface(null, Typeface.BOLD);
        labelFeelingsGroup.setText((String) getGroup(groupPosition));

        return convertView;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return feelingsGroups.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return feelingsGroups.size();
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.feeling_item, parent, false);
        }

        TextView labelFeeling = convertView.findViewById(R.id.label_feeling_item);
        labelFeeling.setText((String) getChild(groupPosition, childPosition));
        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<String> children = mapFeelingsByGroup.get(feelingsGroups.get(groupPosition));
        return children != null ? children.get(childPosition) : null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<String> children = mapFeelingsByGroup.get(feelingsGroups.get(groupPosition));
        return children != null ? children.size() : 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}