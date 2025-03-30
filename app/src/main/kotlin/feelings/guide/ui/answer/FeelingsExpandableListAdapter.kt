package feelings.guide.ui.answer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import feelings.guide.databinding.AnswerFeelingItemBinding
import feelings.guide.databinding.AnswerFeelingsGroupBinding

internal class FeelingsExpandableListAdapter(
    private val context: Context,
    private val feelingsGroups: List<String>,
    private val mapFeelingsByGroup: Map<String, List<String>>
) : BaseExpandableListAdapter() {

    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var groupBinding: AnswerFeelingsGroupBinding
    private lateinit var itemBinding: AnswerFeelingItemBinding

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        view: View?,
        parent: ViewGroup,
    ): View {
        var convertView = view
        val holder: GroupViewHolder
        if (convertView == null) {
            groupBinding = AnswerFeelingsGroupBinding.inflate(inflater)
            convertView = groupBinding.root
            holder = GroupViewHolder()
            holder.label = groupBinding.labelFeelingsGroup
            convertView.tag = holder
        } else {
            holder = convertView.tag as GroupViewHolder
        }
        val labelFeelingsGroup = getGroup(groupPosition) as String
        holder.label!!.text = labelFeelingsGroup
        return convertView
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
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        view: View?,
        parent: ViewGroup,
    ): View {
        var convertView = view
        val holder: ItemViewHolder
        if (convertView == null) {
            itemBinding = AnswerFeelingItemBinding.inflate(inflater)
            convertView = itemBinding.root
            holder = ItemViewHolder()
            holder.label = itemBinding.labelFeelingItem
            convertView.tag = holder
        } else {
            holder = convertView.tag as ItemViewHolder
        }
        val expandedListText = getChild(groupPosition, childPosition) as String
        holder.label!!.text = expandedListText
        return convertView
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

    inner class ItemViewHolder {
        internal var label: TextView? = null
    }

    inner class GroupViewHolder {
        internal var label: TextView? = null
    }
}