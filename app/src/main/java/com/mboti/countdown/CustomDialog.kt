package com.mboti.countdown


import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


val showDialog =   mutableStateOf(false)

@Composable
fun LaunchCountdown(){
    if(showDialog.value){
        DialogCountdown(setShowDialog = { showDialog.value = it }) {
            Log.i("HomePage","HomePage : $it")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = { showDialog.value = true }) {
            Text(text = "Open Dialog")
        }
    }
}



@Composable
fun DialogCountdown(setShowDialog: (Boolean) -> Unit, setValue: (String) -> Unit) {

    Dialog(onDismissRequest = { setShowDialog(false) }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
        ) {

            Box(
                modifier = Modifier.padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                //----------------------------
                //  Paramétrage du CountDown
                //----------------------------
                val totalTime: Long = 3L * 1000L
                val handleColor: Color = MaterialTheme.colorScheme.primary
                val activeBarColor: Color = MaterialTheme.colorScheme.errorContainer
                val inactiveBarColor: Color = MaterialTheme.colorScheme.tertiary
                val modifier: Modifier =  Modifier.size(200.dp)
                val initialValue = 1F
                val strokeWidth: Dp = 5. dp



                //suit la taille du composable ( IntSize.Zeroinitialement).
                var size by remember { mutableStateOf(IntSize.Zero) }
                //Représente la progression actuelle du timer (un flottant entre 0 et 1)
                var value by remember { mutableFloatStateOf(initialValue) }
                //Contient le temps restant en millisecondes (commence par totalTime)
                var currentTime by remember { mutableLongStateOf(totalTime) }
                //Indique si le minuteur est en cours d'exécution (initialement false)
                var isTimerRunning by remember { mutableStateOf(true) }


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

                        if(currentTime == 0L){
                            setShowDialog(false)
                            setValue("PLAY")
                        }
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

                    Button(
                        onClick = {
                            if(currentTime <= 0L) {
                                currentTime = totalTime
                                isTimerRunning = true
                                println("Mounir1-----------> $currentTime")
                            } else {
                                isTimerRunning = !isTimerRunning
                                println("Mounir2-----------> $isTimerRunning")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)

                    ) {
                        val sizeText = if (isTimerRunning && currentTime>0L) {60.sp} else {40.sp}
                        val result = Text(
                            text =
                            //if (isTimerRunning && currentTime>0L) "Stop"
                            if (isTimerRunning && currentTime>0L) (currentTime / 1000L).toString()
                            else if(!isTimerRunning && currentTime>=0L) "resume"
                            else "restart",
                            fontSize = sizeText,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        result.toString()
                    }
                }
            }
        }
    }
}

