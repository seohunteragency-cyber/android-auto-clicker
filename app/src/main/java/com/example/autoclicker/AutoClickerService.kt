package com.example.autoclicker

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AutoClickerService : AccessibilityService() {

    private val TAG = "AutoClickerService"

    // Los textos de los botones que queremos detectar
    private val targetButtonTexts = listOf("Aceptar", "Siguiente", "Continuar", "Click Me")
    
    // Si conocemos los IDs de los botones, podemos añadirlos aquí
    // Ejemplo: "com.android.settings:id/next_button"
    private val targetButtonIds = listOf<String>()

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Analizamos la pantalla cuando cambia el contenido o la ventana
        val rootNode = rootInActiveWindow ?: return
        
        findAndClickButtons(rootNode)
    }

    private fun findAndClickButtons(node: AccessibilityNodeInfo) {
        // Buscamos nodos que sean botones o tengan texto de botón
        if (node.className == "android.widget.Button" || node.isClickable) {
            val text = node.text?.toString() ?: node.contentDescription?.toString() ?: ""
            val viewId = node.viewIdResourceName ?: ""
            
            if (targetButtonTexts.any { it.equals(text, ignoreCase = true) } || 
                targetButtonIds.any { it.equals(viewId, ignoreCase = true) }) {
                Log.d(TAG, "Botón detectado: Text=$text, ID=$viewId")
                clickNode(node)
            }
        }

        // Busqueda recursiva en los hijos
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                findAndClickButtons(child)
            }
        }
    }

    private fun clickNode(node: AccessibilityNodeInfo) {
        // Intento 1: Acción de click directa (si el nodo lo permite)
        if (node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            Log.d(TAG, "Click realizado mediante ACTION_CLICK")
        } else {
            // Intento 2: Simular un gesto de toque en las coordenadas del botón
            val rect = Rect()
            node.getBoundsInScreen(rect)
            val x = rect.centerX().toFloat()
            val y = rect.centerY().toFloat()
            
            dispatchGesture(x, y)
            Log.d(TAG, "Click realizado mediante gesto en ($x, $y)")
        }
    }

    private fun dispatchGesture(x: Float, y: Float) {
        val path = Path()
        path.moveTo(x, y)
        
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 100))
        
        dispatchGesture(gestureBuilder.build(), object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.d(TAG, "Gesto completado")
            }
            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.d(TAG, "Gesto cancelado")
            }
        }, null)
    }

    override fun onInterrupt() {
        Log.e(TAG, "Servicio interrumpido")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Servicio conectado")
    }
}
