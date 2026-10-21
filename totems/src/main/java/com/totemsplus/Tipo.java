package com.totemsplus;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

/** Los tótems nuevos: id del item, nombre, particulas, tono del sonido y lo que hacen (para el texto del item). */
public enum Tipo {

    FUEGO("totem_fuego", "Tótem de Fuego", ParticleTypes.FLAME, 0.8f, new String[]{
            "Revives con 3 corazones.",
            "Resistencia al fuego 5 min y Fuerza 30 s.",
            "Prende fuego y empuja a los monstruos cercanos."}),

    AGUA("totem_agua", "Tótem de Agua", ParticleTypes.BUBBLE_POP, 1.2f, new String[]{
            "Revives con 3 corazones.",
            "Respirar bajo el agua 5 min y Gracia del delfín 2 min.",
            "Regeneración y te apaga el fuego."}),

    VIENTO("totem_viento", "Tótem del Viento", ParticleTypes.CLOUD, 1.4f, new String[]{
            "Revives con 3 corazones.",
            "Sales volando hacia arriba y caes suave 3 min.",
            "Velocidad II y Salto II por 1 min."}),

    VIDA("totem_vida", "Tótem de la Vida", ParticleTypes.HAPPY_VILLAGER, 1.0f, new String[]{
            "Revives con la vida COMPLETA.",
            "Regeneración III y 6 corazones de absorción.",
            "Te llena el hambre un poco."}),

    TRUENO("totem_trueno", "Tótem del Trueno", ParticleTypes.ELECTRIC_SPARK, 0.6f, new String[]{
            "Revives con 3 corazones.",
            "Cae un rayo sobre los monstruos cercanos.",
            "Fuerza II, Velocidad y Resistencia 20 s."}),

    HIELO("totem_hielo", "Tótem de Hielo", ParticleTypes.SNOWFLAKE, 1.1f, new String[]{
            "Revives con 3 corazones.",
            "Resistencia II y absorción 20 s.",
            "Congela (lentitud y debilidad) a los monstruos cercanos."});

    public final String id;
    public final String nombre;
    public final ParticleOptions particula;
    public final float tono;
    public final String[] descripcion;

    Tipo(String id, String nombre, ParticleOptions particula, float tono, String[] descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.particula = particula;
        this.tono = tono;
        this.descripcion = descripcion;
    }
}
