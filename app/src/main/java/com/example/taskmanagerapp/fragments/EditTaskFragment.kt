package com.example.taskmanagerapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.taskmanagerapp.MainActivity
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.databinding.FragmentEditTaskBinding
import com.example.taskmanagerapp.model.Task
import com.example.taskmanagerapp.viewmodel.TaskViewModel


class EditTaskFragment : Fragment(R.layout.fragment_edit_task), MenuProvider {

    private var editTaskBinding: FragmentEditTaskBinding? = null
    private val binding get() = editTaskBinding!!

    private lateinit var tasksViewModel: TaskViewModel
    private lateinit var currentTask: Task

    private val args: EditTaskFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        editTaskBinding = FragmentEditTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        tasksViewModel = (activity as MainActivity).taskViewModel
        currentTask = args.task!!

        // Inside onViewCreated after setting up other views
        val prioritySpinner: Spinner = binding.updtprioritySpinner

// Define the list of priority options
        val priorityOptions = listOf("Low", "Medium", "High")

// Set up the adapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorityOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

// Set the adapter to the Spinner
        prioritySpinner.adapter = adapter

// Set the selected priority
        val currentPriority = currentTask.priority
        val selectedIndex = priorityOptions.indexOf(currentPriority)
        prioritySpinner.setSelection(selectedIndex)

// Listen for item selection events if needed
        prioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPriority = priorityOptions[position]
                // Handle the selected priority as needed
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where nothing is selected
            }
        }

        binding.updatetaskname.setText(currentTask.title)
        binding.updatetime.setText(currentTask.time)
        binding.updatedate.setText(currentTask.date)
        binding.updatedescription.setText(currentTask.description)
        binding.updtprioritySpinner.setSelection(priorityOptions.indexOf(currentTask.priority))

        binding.updatebttn.setOnClickListener{
            val title = binding.updatetaskname.text.toString().trim()
            val description = binding.updatedescription.text.toString().trim()
            val date = binding.updatedate.text.toString().trim()
            val time = binding.updatetime.text.toString().trim()
            val priority = binding.updtprioritySpinner.selectedItem.toString().trim()

            if(title.isNotEmpty()){
                val task = Task(currentTask.id, title, description, date, time, priority)
                tasksViewModel.updateTask(task)
                view.findNavController().popBackStack(R.id.homeFragment, false)
            }else {
                Toast.makeText(context, "Please enter title", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun deleteTask() {
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Task")
            setMessage("Do you want to delete this task?")
            setPositiveButton("Delete"){_,_ ->
                tasksViewModel.deletetask(currentTask)
                Toast.makeText(context, "Task Deleted!", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_task, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.deleteMenu -> {
                deleteTask()
                true
            } else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        editTaskBinding = null
    }

}