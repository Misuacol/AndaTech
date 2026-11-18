package com.example.andatech;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlertasAdapter extends RecyclerView.Adapter<AlertasAdapter.AlertaViewHolder> {

    private List<Alerta> listaAlertas;

    public AlertasAdapter(List<Alerta> listaAlertas) {
        this.listaAlertas = listaAlertas;
    }

    @NonNull
    @Override
    public AlertaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alerta, parent, false);

        return new AlertaViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertaViewHolder holder, int position) {
        Alerta alerta = listaAlertas.get(position);

        holder.titulo.setText(alerta.getTitulo());
        holder.descripcion.setText(alerta.getDescripcion());
        holder.fecha.setText(alerta.getFecha());
    }

    @Override
    public int getItemCount() {
        return listaAlertas.size();
    }

    public static class AlertaViewHolder extends RecyclerView.ViewHolder {

        TextView titulo, descripcion, fecha;

        public AlertaViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.alertaTitulo);
            descripcion = itemView.findViewById(R.id.alertaDescripcion);
            fecha = itemView.findViewById(R.id.alertaFecha);
        }
    }

    public void agregarAlerta(Alerta alerta) {
        listaAlertas.add(0, alerta);
        notifyItemInserted(0);
    }
}