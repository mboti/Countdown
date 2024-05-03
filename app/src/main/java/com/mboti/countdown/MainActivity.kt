package com.mboti.countdown

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mboti.countdown.ui.theme.CountdownTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CountdownTheme {
                Surface(
                    color = Color(0xFF101010),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Timer(
                            totalTime = 3L * 1000L,
                            handleColor = Color.Green,
                            inactiveBarColor = Color.DarkGray,
                            activeBarColor = Color(0xFF37B900),
                            modifier = Modifier.size(200.dp)
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun  Timer (
    totalTime: Long,
    handleColor: Color,
    activeBarColor: Color,
    inactiveBarColor: Color,
    modifier: Modifier = Modifier,
    initialValue: Float = 1F,
    strokeWidth: Dp = 5. dp
) {
    //suit la taille du composable ( IntSize.Zeroinitialement).
    var size by remember { mutableStateOf(IntSize.Zero) }
    //Représente la progression actuelle du timer (un flottant entre 0 et 1)
    var value by remember { mutableFloatStateOf(initialValue) }
    //Contient le temps restant en millisecondes (commence par totalTime)
    var currentTime by remember { mutableLongStateOf(totalTime) }
    //Indique si le minuteur est en cours d'exécution (initialement false)
    var isTimerRunning by remember { mutableStateOf(false) }


    /**
     * Si la minuterie est en cours d'exécution ( isTimerRunning) et qu'il reste du temps ( currentTime> 0),
     * elle décrémente currentTimetoutes les 100 millisecondes et met à jour en valueconséquence
     * (en divisant par totalTimepour obtenir une progression de 0 à 1). Cela permet de suivre efficacement
     * la progression du chronomètre
     */
    //LaunchedEffectobserve des changements dans currentTimeet isTimerRunning
    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
        if (currentTime > 0 && isTimerRunning) {
            delay( 100L )
            currentTime -= 100L
            value = currentTime / totalTime.toFloat()
        }
    }


    /**
     * Combinez tous ensemble dans – La disposition de la boîte
     *
     * Le Timercomposable combine ces éléments dans un Boxwith contentAlignmentset to Alignment.Center.
     * Les Canvas, Textet Buttonsont positionnés à l'intérieur des Boxmodificateurs et des alignements.
     */
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.onSizeChanged {
            size = it
        }
    ){

        /**
         * Dessiner la minuterie avec Canvas
         * -----------------------------------
         * 1/ Un Canvascomposable vous permet de dessiner des formes et des visuels personnalisés.
         * 2/ Au sein du Canvas:
         * Dessinez deux arcs :
         * Un arc extérieur inactiveBarColorreprésente la partie inactive de la barre du minuteur.
         * Un arc intérieur utilisant activeBarColorreprésente le temps restant, avec sa taille déterminée par le value.
         * Dessinez la poignée de la minuterie sous forme de cercle à la fin de l'arc actif en utilisant drawPointsavec handleColor
         */
        Canvas(modifier = modifier){
            drawArc(
                color = inactiveBarColor,
                startAngle = -215f,
                sweepAngle = 250f,
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(strokeWidth.toPx(),cap= StrokeCap.Round)
            )
            drawArc(
                color = activeBarColor,
                startAngle = -215f,
                sweepAngle = 250f * value,
                useCenter = false,
                size = Size(size.width.toFloat(), size.height.toFloat()),
                style = Stroke(strokeWidth.toPx(),cap= StrokeCap.Round)
            )
            val center = Offset(size.width/2f,size.height/2f)
            val beta = (250f * value + 145f) * (PI / 180f).toFloat()
            val r = size.width/2f
            val a = cos(beta) * r
            val b = sin(beta) * r

            drawPoints(
                listOf(Offset(center.x + a,center.y + b)),
                pointMode = PointMode.Points,
                color = handleColor,
                strokeWidth = (strokeWidth * 3f).toPx(),
                cap = StrokeCap.Round
            )
        }

        /**
         * Étape 5 : Affichage du temps restant et du bouton
         *
         * Un Textcomposable affiche le temps restant en secondes ( currentTime / 1000L).
         * A Buttonpermet aux utilisateurs de démarrer, d'arrêter ou de redémarrer le minuteur :
         * La couleur du bouton change en fonction de l'état de la minuterie en utilisantButtonDefaults.buttonColors :
         * Vert pour le démarrage ou le redémarrage lorsque la minuterie ne fonctionne pas ou a atteint zéro.
         * Rouge pour arrêter lorsque la minuterie est en marche.
         * Le texte du bouton change également en fonction de l'état du minuteur (à l'aide d'une instruction if).
         */
//        Text(
//            text = (currentTime / 1000L).toString(),
//            fontSize = 44.sp,
//            fontWeight = FontWeight.Bold,
//            color = Color.White
//        )
        Button(
            onClick = {
                if(currentTime <= 0L) {
                    currentTime = totalTime
                    isTimerRunning = true
                } else {
                    isTimerRunning = !isTimerRunning
                }
            },
            //modifier = Modifier.align(Alignment.CenterVertically),
            colors = ButtonDefaults.buttonColors(containerColor =
            if (!isTimerRunning || currentTime <= 0L) {
                Color.Green
            }
            else {
                Color.Transparent
            }
            )
        ) {
            val sizeText = if (isTimerRunning && currentTime>0L) {60.sp} else {20.sp}
            Text(
                text =
                //if (isTimerRunning && currentTime>0L) "Stop"
                if (isTimerRunning && currentTime>0L) (currentTime / 1000L).toString()
                else if(!isTimerRunning && currentTime>=0L) "start"
                else "restart",
                fontSize = sizeText,
                fontWeight = FontWeight.Bold,
            )
        }

    }


}


@Preview
@Composable
fun DefaultPreviewTimer() {
    Surface(
        color = Color(0xFF101010),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Timer(
                totalTime = 10L * 1000L,
                handleColor = Color.Green,
                inactiveBarColor = Color.DarkGray,
                activeBarColor = Color(0xFF37B900),
                modifier = Modifier.size(200.dp)
            )
        }
    }
}

