package com.example.taskmanagerapp.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.example.taskmanagerapp.MainActivity
import com.example.taskmanagerapp.R
import com.example.taskmanagerapp.databinding.FragmentAddTaskBinding
import com.example.taskmanagerapp.model.Task
import com.example.taskmanagerapp.viewmodel.TaskViewModel
import java.util.Calendar


class AddTaskFragment : Fragment(R.layout.fragment_add_task), MenuProvider {

    private var addTaskBinding: FragmentAddTaskBinding? = null
    private val binding get() = addTaskBinding!!

    private lateinit var tasksViewModel: TaskViewModel
    private lateinit var addTaskView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addTaskBinding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        tasksViewModel = (activity as MainActivity).taskViewModel
        addTaskView = view

        val prioritySpinner: Spinner = view.findViewById(R.id.prioritySpinner)
        val categorySpinner: Spinner = view.findViewById(R.id.categorySpinner)

        val priorityOptions = arrayOf("High", "Medium", "Low")
        val categoryOptions = arrayOf("Personal", "Work", "Travel", "Finance")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorityOptions)
        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categoryOptions)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        prioritySpinner.adapter = adapter
        categorySpinner.adapter = adapter1

        binding.date.setOnClickListener{
            showDatePicker()
        }

        binding.time.setOnClickListener{
            showTimePicker()
        }

    }

    private fun showDatePicker() {
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(),
            {_, year, month, dayOfMonth ->
                val selectedDate = "${dayOfMonth}/${month + 1}/${year}"
                binding.date.setText(selectedDate)
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val selectedTime = "$selectedHour:$selectedMinute"
                binding.time.setText(selectedTime)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun saveTask(view: View){
        val title = binding.tskname.text.toString().trim()
        val description = binding.description.text.toString().trim()
        val time = binding.time.text.toString().trim()
        val date = binding.date.text.toString().trim()
        val priority = binding.prioritySpinner.selectedItem.toString().trim()
        val category = binding.categorySpinner.selectedItem.toString().trim()

        if(title.isNotEmpty()){
            val task = Task(0, title, description, date, time, priority, category)
            tasksViewModel.addTask(task)

            Toast.makeText(addTaskView.context, "Task Added", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment, false)
        } else {
            Toast.makeText(addTaskView.context, "Please Enter Task Title", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_task, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.saveMenu -> {
                saveTask(addTaskView)
                true
            }
            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        addTaskBinding = null
    }


}