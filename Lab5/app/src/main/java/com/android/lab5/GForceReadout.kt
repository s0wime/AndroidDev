package com.android.lab5

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.lab5.ui.theme.AxisXColor
import com.android.lab5.ui.theme.AxisYColor
import com.android.lab5.ui.theme.AxisZColor

@Composable
fun GForceReadout(state: GForceState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            "Current",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            AxisValue("X", state.current.x, AxisXColor)
            AxisValue("Y", state.current.y, AxisYColor)
            AxisValue("Z", state.current.z, AxisZColor)
            AxisValue("Total", state.current.magnitude, MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Max",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            AxisValue("X", state.maxX, AxisXColor)
            AxisValue("Y", state.maxY, AxisYColor)
            AxisValue("Z", state.maxZ, AxisZColor)
            AxisValue("Total", state.maxMagnitude, MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun AxisValue(label: String, value: Float, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
        Text(
            "%.2f G".format(value),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
