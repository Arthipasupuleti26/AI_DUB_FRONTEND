package com.simats.aidub.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.simats.aidub.model.Project

/**
 * Repository for managing Project persistence using SharedPreferences.
 */
class ProjectRepository(context: Context) {
    
    private val sharedPreferences = context.getSharedPreferences("Projects", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val projectsKey = "projects_list"
    
    /**
     * Save a new project to storage.
     */
    fun saveProject(project: Project) {
        val projects = getRecentProjects().toMutableList()
        projects.add(0, project) // Add to beginning (newest first)
        saveProjects(projects)
    }
    
    /**
     * Get a specific project by ID.
     */
    fun getProject(id: String): Project? {
        return getRecentProjects().find { it.id == id }
    }
    
    /**
     * Get all recent projects, sorted by creation date (newest first).
     */
    fun getRecentProjects(): List<Project> {
        val json = sharedPreferences.getString(projectsKey, null) ?: return emptyList()
        val type = object : TypeToken<List<Project>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Delete a project by its ID.
     */
    fun deleteProject(id: String) {
        val projects = getRecentProjects().toMutableList()
        projects.removeAll { it.id == id }
        saveProjects(projects)
    }
    
    /**
     * Update the status of a project.
     */
    fun updateProjectStatus(id: String, newStatus: String) {
        val projects = getRecentProjects().toMutableList()
        val index = projects.indexOfFirst { it.id == id }
        if (index != -1) {
            projects[index] = projects[index].copy(status = newStatus)
            saveProjects(projects)
        }
    }
    
    /**
     * Update the processing progress of a project.
     */
    fun updateProjectProgress(id: String, stage: String, progress: Int) {
        val projects = getRecentProjects().toMutableList()
        val index = projects.indexOfFirst { it.id == id }
        if (index != -1) {
            projects[index] = projects[index].copy(
                processingStage = stage,
                processingProgress = progress
            )
            saveProjects(projects)
        }
    }
    
    /**
     * Mark a project as complete.
     */
    fun markProjectComplete(id: String) {
        val projects = getRecentProjects().toMutableList()
        val index = projects.indexOfFirst { it.id == id }
        if (index != -1) {
            projects[index] = projects[index].copy(
                status = "Ready",
                processingStage = "complete",
                processingProgress = 100
            )
            saveProjects(projects)
        }
    }
    
    /**
     * Check if there are any projects.
     */
    fun hasProjects(): Boolean = getRecentProjects().isNotEmpty()
    
    private fun saveProjects(projects: List<Project>) {
        val json = gson.toJson(projects)
        sharedPreferences.edit().putString(projectsKey, json).apply()
    }
}

