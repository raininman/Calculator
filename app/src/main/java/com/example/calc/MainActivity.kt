package com.example.calc

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.calc.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var lastNumeric = false
    var stateError = false
    var lastDot = false
    var status = ""
    var tvStatus = ""
    private val statusVariableKey = "STATUS_VARIABLE"
    private val historyVariableKey = "HISTORY_VARIABLE"
    private val numericVariableKey = "NUMERIC_VARIABLE"
    private val dotVariableKey = "DOT_VARIABLE"
    private val errorVariableKey = "ERROR_VARIABLE"

    var history = ""

    private lateinit var expression: Expression


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(statusVariableKey, tvStatus)
        outState.putBoolean(numericVariableKey, lastNumeric)
        outState.putBoolean(dotVariableKey, lastDot)
        outState.putBoolean(errorVariableKey, stateError)
        outState.putString(historyVariableKey, history)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        tvStatus = savedInstanceState.getString(statusVariableKey).toString()
        history = savedInstanceState.getString(historyVariableKey).toString()
        lastDot = savedInstanceState.getBoolean(dotVariableKey)
        lastNumeric = savedInstanceState.getBoolean(numericVariableKey)
        stateError = savedInstanceState.getBoolean(errorVariableKey)
        binding.tvStatus.text = tvStatus
        if(tvStatus != ""){
            lastNumeric = true
        }else{
            lastNumeric = false
        }
    }

    private fun saveName(view: View) {
        val status = binding.tvStatus
        tvStatus = status.text.toString()
    }

    private fun saveHistory(view: View){
        history = binding.tvStatus.text.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onAllClearClick(view: View){
        binding.tvStatus.text = ""
        binding.tvResult.text = ""
        tvStatus = ""
        lastNumeric = false
        stateError=false
        lastDot = false
        saveName(view)
        binding.tvResult.visibility = View.GONE
        status = binding.tvStatus.text.toString()
    }


    fun onClearClick(view: View){

        binding.tvStatus.text = ""
        tvStatus = ""
        lastNumeric = false
        status = binding.tvStatus.text.toString()
        saveName(view)
    }


    fun onBackClick(view: View){
        binding.tvStatus.text = binding.tvStatus.text.toString().dropLast(1)
        try{
            val lastChar = binding.tvStatus.text.last()
            if(lastChar.isDigit()){
                status = binding.tvStatus.text.toString()
                saveName(view)
            }
        }catch (e:Exception){
            binding.tvResult.text = ""
            binding.tvResult.visibility = View.GONE
            Log.e("Last char error", e.toString())
        }

    }

    fun onEqualClick(view: View){
        onEqual(view)
        binding.tvStatus.text = binding.tvResult.text.toString().drop(1)
        saveName(view)
        saveHistory(view)
    }

    fun onHistoryClick(view: View){\
        binding.tvStatus.text = history
        saveName(view)
    }


    fun onDigitClick(view: View){

        if(stateError){
            binding.tvStatus.text = (view as Button).text
            stateError = false
        }else{
            binding.tvStatus.append((view as Button).text)
        }
        lastNumeric = true
        status = binding.tvStatus.text.toString()
        saveName(view)
    }

    fun onDotClick(view: View){
        if(stateError){
            binding.tvStatus.text = (view as Button).text
            stateError = false
        }else if(!lastDot){
            binding.tvStatus.append((view as Button).text)
        }
        lastDot = true
        status = binding.tvStatus.text.toString()
        saveName(view)
    }

    fun onOperatorClick(view: View) {
        saveName(view)
        if (!stateError && lastNumeric) {
            binding.tvStatus.append((view as Button).text)
            lastDot = false
            lastNumeric = false
            status = binding.tvStatus.text.toString()
        }
    }

    fun onStartOperatorClick(view: View){
        if(!stateError){
            binding.tvStatus.append((view as Button).text)
            lastDot = false
            lastNumeric = false
            status = binding.tvStatus.text.toString()
            saveName(view)
        }
    }

    private fun onEqual(view: View){
        if(!stateError){
            val txt = binding.tvStatus.text.toString()
            expression = ExpressionBuilder(txt).build()
            try{
                val result = expression.evaluate()
                binding.tvResult.visibility = View.VISIBLE
                binding.tvResult.text = "=" + result.toString()
                saveName(view)
            }catch (ex:ArithmeticException){
                Log.e("Evaluate errors", ex.toString())
                binding.tvResult.text = "EError"
                stateError = true
                lastNumeric = false
            }
        }else{
            binding.tvStatus.text = "Error"
        }
    }



}