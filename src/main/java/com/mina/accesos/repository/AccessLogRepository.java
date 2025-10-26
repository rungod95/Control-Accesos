package com.mina.accesos.repository;

import com.mina.accesos.domain.AccessLog;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    List<AccessLog> findByFechaHoraSalidaIsNullOrderByFechaHoraEntradaDesc();

    long countByFechaHoraSalidaIsNull();

    long countByFechaHoraEntradaBetween(LocalDateTime start, LocalDateTime end);

    @Query("select a.tipoUsuario as tipo, count(a) as total from AccessLog a group by a.tipoUsuario")
    List<Object[]> countGroupByTipoUsuario();

    @Query("""
            select a from AccessLog a
            where (:desde is null or a.fechaHoraEntrada >= :desde)
              and (:hasta is null or a.fechaHoraEntrada <= :hasta)
              and (:tipo is null or lower(a.tipoUsuario) = :tipo)
              and (:qr is null or lower(a.qrCode) like lower(concat('%', :qr, '%')))
            order by a.fechaHoraEntrada desc
            """)
    List<AccessLog> search(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("tipo") String tipoUsuario,
            @Param("qr") String qrCode);
}
