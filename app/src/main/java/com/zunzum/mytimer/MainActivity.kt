package com.zunzum.mytimer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zunzum.mytimer.ui.theme.MyTimerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TimerScreen()
                }
            }
        }
    }
}

@Composable
fun TimerScreen() {
    //데이터
    //타이머카운트
    var timerCount by remember { mutableStateOf(0) }

    //활성화여부
    var isActive by remember { mutableStateOf(false) }

    //토스트 띄우기 위해 필요한 SnackbarHostState(스낵바 상태 열림 닫힘 등) 추가
    val snackbarHostState = remember { SnackbarHostState()}

    val coroutineScope = rememberCoroutineScope()

    //timerScreen이 실행되면 자동적으로 실행되는 코루틴
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L) // 1초 딜레이
            //isActive가 true(줄여서 그냥 isActive로 표현)이고 timerCount가 0보다 크면 timerCount 1씩 감소
            if (isActive && timerCount > 0) timerCount --
            if (isActive && timerCount == 0) isActive = false
        }
    }

    //전체 내용 배치할 큰 열 만들기
    Column(
        //배치 중앙으로 설정
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //텍스트를 위에 설정함 timerCount를 Stirng값으로 변경하여 넣고 폰ㄴ트사이즈, 패딩 설정
        Text(timerCount.toString(),
            fontSize = 120.sp,
            modifier = Modifier.padding(vertical = 100.dp)
        )

        //타이머 토글버튼
        Button(onClick = {
            //버튼 눌렀을때 실행되는 부분

            //스낵바가 올라와있으면 아래 로직 타지 말고 리턴해라
            if (snackbarHostState.currentSnackbarData != null) return@Button

            //timerCount가 0이면 로직이 안타고 넘어가버리게 설정
            if (timerCount==0) {
                coroutineScope.launch {
                    snackbarHostState
                        .showSnackbar(
                            "⏰ 시간을 먼저 설정해주세요",
                        actionLabel = "확인", SnackbarDuration.Short
                        ).let {
                            when(it) {
                                SnackbarResult.Dismissed -> Log.d("TAG", "스낵바 닫힘")
                                SnackbarResult.ActionPerformed -> Log.d("TAG", "스낵바 확인버튼 클릭")
                            }
                        }
                    return@launch
                }
                return@Button
            }

            //isActive가 true(실행중)이고 timerCount가 0보다 클때 timerCount를 0으로 바꿈
            if (isActive && timerCount > 0) timerCount=0

            //isActive가 bool형일때 반대의 상태로 만들어 주는 식
            isActive = !isActive
        }) {
            //버튼 내부 표시될 부분, 텍스트로 시작, 폰트사이즈, 패딩 설정
            Text(if (isActive) "종료" else "시작",
                fontSize = 30.sp,
                modifier = Modifier.padding(10.dp))
        }

        AnimatedVisibility(visible = !isActive) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("타이머 설정",
                    fontSize = 30.sp,
                    modifier = Modifier.padding(10.dp))

                Row() {
                    //시간 증가 버튼
                    Button(
                        modifier = Modifier.size(80.dp),
                        onClick = {
                            //버튼 눌렀을때 실행되는 부분
                            //TODO :: 시간증가
                            timerCount ++
                        }) {
                        //버튼 내부 표시될 부분, 텍스트로 시작, 폰트사이즈, 패딩 설정
                        Text("+",
                            fontSize = 30.sp,
                            modifier = Modifier.padding(10.dp))
                    }

                    //공간주기
                    Spacer(modifier = Modifier.width(30.dp))

                    //시간 감소 버튼
                    Button(
                        modifier = Modifier.size(80.dp),
                        onClick = {
                            //버튼 눌렀을때 실행되는 부분
                            //TODO :: 시간 감소
                            if (timerCount > 0) timerCount--
                        }) {
                        //버튼 내부 표시될 부분, 텍스트로 시작, 폰트사이즈, 패딩 설정
                        Text("-",
                            fontSize = 30.sp,
                            modifier = Modifier.padding(10.dp))
                    }

                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        //스낵바가 보여지는 부분
        SnackbarHost(hostState = snackbarHostState)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyTimerTheme {

    }
}