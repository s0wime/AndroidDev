package com.android.lab2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import com.android.lab2.databinding.FragmentInputBinding

class InputFragment : Fragment() {

    private var _binding: FragmentInputBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonOk.setOnClickListener {
            val departure = binding.editDeparture.text.toString()
            val arrival = binding.editArrival.text.toString()
            val selectedRadioId = binding.radioGroupTime.checkedRadioButtonId
            val selectedTime = view.findViewById<RadioButton>(selectedRadioId).text.toString()

            val result = "Маршрут: $departure - $arrival\nЧас відправлення: $selectedTime"

            (activity as? MainActivity)?.showResult(result)
        }
    }

    fun clearForm() {
        binding.editDeparture.text?.clear()
        binding.editArrival.text?.clear()
        binding.radioGroupTime.check(R.id.radioMorning)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
