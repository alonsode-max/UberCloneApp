package com.example.ubercloneapp

import org.junit.Assert.assertEquals
import org.junit.Test

class PriceCalculatorTest {
    private fun calculatePrice(distanceKm:Double):Double{
        val baseFare=2.50
        val perKmRate=1.20
        val minFare=5.00
        val calculated=baseFare+(distanceKm*perKmRate)
        return maxOf(calculated,minFare)
    }

    @Test
    fun `precio de viaje de 5km es correcto`(){
        val price=calculatePrice(5.0)
        assertEquals(8.50,price,0.01)
    }

    @Test
    fun `precio minimo se aplica en viajes cortos`(){
        val price=calculatePrice(1.0)
        assertEquals(5.00,price,0.01)
    }

    @Test
    fun `distancia cero tiene tarifa minima`() {
        val price = calculatePrice(0.0)
        assertEquals(5.00, price, 0.01)
    }
}