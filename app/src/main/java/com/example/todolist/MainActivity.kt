package com.example.todolist

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.todolist.ui.theme.ToDoListTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val tasks = remember {
                        mutableStateListOf<Task>()
                    }

                    var currentTaskText by remember {mutableStateOf("")}

                    val focusManager = LocalFocusManager.current

                    Column {

                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){

                            TextField(
                                label = {Text(stringResource(R.string.new_task))},
                                value = currentTaskText,
                                onValueChange = {currentTaskText = it},
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Done
                                ),
                                modifier = Modifier
                                    .width(150.dp),
                                singleLine = true
                            )

                            Image(
                                painter = painterResource(id = R.drawable.plus_icon),
                                contentDescription = "Plus Button",
                                Modifier
                                    .clickable{
                                    tasks.add(Task(text = currentTaskText))
                                    currentTaskText = ""
                                    focusManager.clearFocus()
                                }
                                    .width(40.dp)
                                    .height(40.dp)
                            )

                            Image(
                                painter = painterResource(id = R.drawable.save_icon),
                                contentDescription = "Save Button",
                                Modifier
                                    .clickable{
                                    saveTasks(this@MainActivity, tasks)
                                }
                                    .width(40.dp)
                                    .height(40.dp)
                            )

                            Image(
                                painter = painterResource(id = R.drawable.load_icon),
                                contentDescription = "Load Button",
                                Modifier
                                    .clickable{
                                    tasks.clear()
                                    tasks.addAll(loadTasks(this@MainActivity))
                                }
                                    .width(40.dp)
                                    .height(40.dp)
                            )
                        }

                        TaskColumn(
                            tasks = tasks,
                            onDelete = {tasks.remove(it)}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskColumn(
    tasks: MutableList<Task>,
    modifier: Modifier = Modifier,
    onDelete: (Task) -> Unit
){
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 16.dp, end = 16.dp)
    ){

        items(tasks, key = { task -> task.id }){
            task -> TaskElement(task = task, onDelete = onDelete, modifier = modifier)
        }
    }
}

@Composable
fun TaskElement(task: Task, onDelete: (Task) -> Unit, modifier: Modifier = Modifier){

    var taskText by remember { mutableStateOf(task.GetText()) }
    var taskStatus by remember {mutableStateOf(task.GetStatus())}

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
    ){

        TextField(
            value = taskText,
            onValueChange = {
                taskText = it
                task.SetText(it)
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            modifier = Modifier
                .width(200.dp),
            singleLine = true
        )

        Checkbox(
            checked = taskStatus,
            onCheckedChange = {
                taskStatus = it
                task.SetStatus(it)
            },
            colors = CheckboxDefaults.colors(checkmarkColor = White,
                checkedColor = Black)
        )

        Image(
            painter = painterResource(R.drawable.delete_icon),
            contentDescription = "delete button",
            modifier = Modifier
                .clickable { onDelete(task) }
                .width(30.dp)
                .height(30.dp)
        )
    }
}

fun saveTasks(context: Context, tasks: List<Task>) {
    val gson = Gson()
    val json = gson.toJson(tasks)

    try {
        val file = File(context.filesDir, "tasks.json")
        file.writeText(json)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun loadTasks(context: Context): List<Task> {
    val gson = Gson()
    val file = File(context.filesDir, "tasks.json")

    return if (file.exists()) {
        try {
            val json = file.readText()
            val type = object : TypeToken<List<Task>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    } else {
        emptyList()
    }
}