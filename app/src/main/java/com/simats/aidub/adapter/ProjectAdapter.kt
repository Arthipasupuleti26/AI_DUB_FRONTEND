package com.simats.aidub.adapter

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simats.aidub.R
import com.simats.aidub.model.Project

/**
 * Adapter for displaying projects in a RecyclerView.
 */
class ProjectAdapter(
    private var projects: List<Project> = emptyList(),
    private val onProjectClick: (Project) -> Unit = {}
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    fun updateProjects(newProjects: List<Project>) {
        projects = newProjects
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    override fun getItemCount(): Int = projects.size

    inner class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivThumbnail: ImageView = itemView.findViewById(R.id.iv_thumbnail)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_project_title)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tv_timestamp)
        private val tvStatus: TextView = itemView.findViewById(R.id.tv_status)

        fun bind(project: Project) {
            tvTitle.text = project.title
            tvTimestamp.text = getRelativeTime(project.createdAt)
            
            // Set status badge
            when (project.status) {
                "Ready" -> {
                    tvStatus.text = "Ready"
                    tvStatus.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                    tvStatus.setBackgroundResource(R.drawable.bg_status_ready)
                }
                else -> {
                    tvStatus.text = "Processing"
                    tvStatus.setTextColor(0xFFD97706.toInt())
                    tvStatus.setBackgroundResource(R.drawable.bg_status_processing)
                }
            }
            
            // Try to load video thumbnail
            try {
                val uri = Uri.parse(project.videoUri)
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(itemView.context, uri)
                val bitmap: Bitmap? = retriever.getFrameAtTime(1000000) // 1 second
                if (bitmap != null) {
                    ivThumbnail.setImageBitmap(bitmap)
                }
                retriever.release()
            } catch (e: Exception) {
                // Keep default placeholder on error
                ivThumbnail.setImageResource(R.drawable.img_thumb_tech)
            }
            
            itemView.setOnClickListener { onProjectClick(project) }
        }

        private fun getRelativeTime(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp
            
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            
            return when {
                seconds < 60 -> "Just now"
                minutes < 60 -> "$minutes min ago"
                hours < 24 -> "$hours hours ago"
                days == 1L -> "Yesterday"
                days < 7 -> "$days days ago"
                else -> "${days / 7} weeks ago"
            }
        }
    }
}
