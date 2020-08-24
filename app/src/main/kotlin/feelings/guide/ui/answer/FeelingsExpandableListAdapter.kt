package feelings.guide.ui.answer

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import feelings.guide.R
import kotlinx.android.synthetic.main.answer_feeling_item.view.*
import kotlinx.android.synthetic.main.answer_feelings_group.view.*

internal class FeelingsExpandableListAdapter(
        private val context: Context,
        private val feelingsGroups: List<String>,
        private val mapFeelingsByGroup: Map<String, List<String>>
) : BaseExpandableListAdapter() {

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        val cv = convertView
                ?: LayoutInflater.from(context).inflate(R.layout.answer_feelings_group, parent, false)
        cv.labelFeelingsGroup.apply {
            setTypeface(null, Typeface.BOLD)
            text = getGroup(groupPosition) as String
        }
        return cv
    }

    override fun getGroup(groupPosition: Int): Any {
        return feelingsGroups[groupPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return feelingsGroups.size
    }

    override fun getChildView(
            groupPosition: Int, childPosition: Int,
            isLastChild: Boolean, convertView: View?, parent: ViewGroup
    ): View {
        val cv = convertView
                ?: LayoutInflater.from(context).inflate(R.layout.answer_feeling_item, parent, false)
        cv.labelFeelingItem?.text = getChild(groupPosition, childPosition) as String
        return cv
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any? {
        val children = mapFeelingsByGroup[feelingsGroups[groupPosition]]
        return if (children != null) children[childPosition] else null
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val children = mapFeelingsByGroup[feelingsGroups[groupPosition]]
        return children?.size ?: 0
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}