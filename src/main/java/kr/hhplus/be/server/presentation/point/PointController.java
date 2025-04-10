package kr.hhplus.be.server.presentation.point;

import jakarta.validation.Valid;
import kr.hhplus.be.server.application.PointFacade;
import kr.hhplus.be.server.application.PointResult;
import kr.hhplus.be.server.application.dto.PointCriteria;
import kr.hhplus.be.server.presentation.point.object.PointHistoryResponse;
import kr.hhplus.be.server.presentation.point.object.PointRequest;
import kr.hhplus.be.server.presentation.point.object.PointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/point")
public class PointController implements PointApi {
    private final PointFacade pointFacade;

    @PostMapping("/getPoint")
    public ResponseEntity<PointResponse> getPoint(@Valid @RequestBody PointRequest pointRequest) {
        PointResult pointResult = pointFacade.getPoint(pointRequest.toCriteria());
        return new ResponseEntity<>(PointResult.toPointResponse(pointResult), HttpStatus.OK);
    }

    @PostMapping("/charge")
    public ResponseEntity.BodyBuilder charge(@Valid @RequestBody PointRequest pointRequest) {
        pointFacade.chargePoint(pointRequest.toCriteria());

        return ResponseEntity.status(HttpStatus.OK);
    }

    @PostMapping("/history")
    public ResponseEntity<List<PointHistoryResponse>> history(@Valid @RequestBody PointRequest pointRequest) {
        List<PointHistoryResponse> list = pointFacade.getPointHistory(PointCriteria.from(pointRequest))
                .stream().map(ph -> new PointHistoryResponse(
                        ph.pointId(),
                        ph.type(),
                        ph.createdAt()
                )).toList();

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
