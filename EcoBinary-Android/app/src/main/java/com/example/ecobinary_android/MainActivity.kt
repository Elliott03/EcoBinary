package com.example.ecobinary_android

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ecobinary_android.interfaces.RetrofitAPI
import com.example.ecobinary_android.models.CommandModel
import com.example.ecobinary_android.models.OutputModel
import com.example.ecobinary_android.ui.theme.DarkOutputGray
import com.example.ecobinary_android.ui.theme.DefaultGray
import com.example.ecobinary_android.ui.theme.EcoBinaryAndroidTheme
import com.example.ecobinary_android.ui.theme.OutputGray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcoBinaryAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}
fun onExecute(commands: String, privateIp: String, portNumber: String, onResult: (String) -> Unit) {
    var commandList: List<String> = commands.split("\n")

    val retrofit = Retrofit.Builder()
        .baseUrl("http://${privateIp}:${portNumber}")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(RetrofitAPI::class.java)
    val commandModel = CommandModel(commandList)

    apiService.postData(commandModel)!!.enqueue(object : Callback<String> {
        override fun onResponse(call: Call<String>, response: Response<String>) {
            val result = response.body() ?: "Empty response body"
            println(response.body())
            println("-----------------")
            onResult(result)
        }

        override fun onFailure(call: Call<String>, t: Throwable) {
            val result = t.message ?: "Error"
            println(t.message)
            println(t.cause)
            onResult(result)
        }
    })
}
@Composable
fun App() {
    var commands: String by remember { mutableStateOf("") }
    var privateIp: String by remember { mutableStateOf("") }
    var portNumber: String by remember{ mutableStateOf("") }
    var commandsOutput: String by remember { mutableStateOf("") }
    val screenHeight = LocalConfiguration.current.screenHeightDp

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .height(screenHeight.dp)
            .padding(10.dp, 0.dp)

    ) {
        TopBar(privateIp, portNumber, onPrivateIpChange = { newPrivateIp -> privateIp = newPrivateIp}, onPortNumberChange = { newPortNumber -> portNumber = newPortNumber})

        CommandInput(commandsOutput) {newCommands -> commands = newCommands}
        Button(
            onClick = { onExecute(commands, privateIp, portNumber) {it
                commandsOutput = it
            } },
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .padding(0.dp, 5.dp, 0.dp, 0.dp)

        ) {
            Text(
                text = "Execute"
            )
        }
    }

}

@Composable
fun TopBar(privateIp: String, portNumber: String, onPrivateIpChange: (String) -> Unit, onPortNumberChange: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Absolute.SpaceBetween,
        modifier = Modifier
            .padding(0.dp, 0.dp, 0.dp, 10.dp)
    ) {
        IpForm(privateIp, onPrivateIpChange)
        Title()
        PortForm(portNumber, onPortNumberChange)
    }
}
@Composable
fun IpForm(privateIp: String, onPrivateIpChange: (String) -> Unit) {
    var ip by remember { mutableStateOf("") }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Private IP:", textAlign = TextAlign.Center)
        TextField(
            value = ip,
            onValueChange = {
                ip = it
                onPrivateIpChange(ip) },
            modifier = Modifier
                .width(150.dp)
                .padding(5.dp, 5.dp, 0.dp, 0.dp)
        )
    }
}
@Composable
fun Title() {
    Text(text = "EcoBinary")
}

@Composable
fun PortForm(portNumber: String, onPortNumberChange: (String) -> Unit) {
    var port by remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Port:")
        TextField(
            value = port,
            onValueChange = {
                port = it
                onPortNumberChange(port)
                            },
            modifier = Modifier
                .width(150.dp)
                .padding(5.dp, 5.dp, 0.dp, 0.dp)
        )
    }
}
@Composable
fun CommandInput(commandsOutput: String, onCommandsChange: (String) -> Unit) {
    var command by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    var outputTextHeight: Dp = 0.dp;
    var inputTextHeight: Dp = 600.dp
    if (commandsOutput != "") {
        outputTextHeight = 200.dp
        inputTextHeight = 400.dp
    }
    BoxWithConstraints(
        modifier = Modifier
            .background(DarkOutputGray)
            .fillMaxWidth()
    ) {
        Text(
            text = commandsOutput,
            modifier = Modifier
                .padding(10.dp, 0.dp)
                .height(outputTextHeight)
                .verticalScroll(scrollState),
                color = Color.White

        )
    }

    TextField(
        value = command, onValueChange ={
            command = it
            onCommandsChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .height(inputTextHeight),
        label = {Text(text = "users/elliottphillips/Desktop/EcoBinary/EcoBinary-Android")},
        placeholder = { Text(text = "Give command here")},
        shape = RoundedCornerShape(0.dp)
    ) 
}
