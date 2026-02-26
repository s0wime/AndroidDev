package com.android.lab3

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.lab3.databinding.FragmentInputBinding

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
            val departure = binding.editDeparture.text.toString().trim()
            val arrival = binding.editArrival.text.toString().trim()

            if (departure.isEmpty() || arrival.isEmpty()) {
                Toast.makeText(requireContext(), "Заповніть усі поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRadioId = binding.radioGroupTime.checkedRadioButtonId
            val selectedTime = view.findViewById<RadioButton>(selectedRadioId).text.toString()

            val result = "Маршрут: $departure - $arrival\nЧас відправлення: $selectedTime"

            val dbHelper = TrainDbHelper(requireContext())
            val id = dbHelper.insert(departure, arrival, selectedTime)

            if (id != -1L) {
                Toast.makeText(requireContext(), "Запис збережено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Помилка запису", Toast.LENGTH_SHORT).show()
            }

            (activity as? MainActivity)?.showResult(result)
        }

        binding.buttonOpen.setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
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
