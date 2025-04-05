package kr.hhplus.be.server.presentation.point;

import jakarta.validation.Valid;
import kr.hhplus.be.server.presentation.point.object.PointRequest;
import kr.hhplus.be.server.presentation.point.object.PointResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/point")
public class PointController implements PointApi {
    @GetMapping("/getPoint")
    public ResponseEntity<PointResponse> getPoint(@Valid @RequestBody PointRequest pointRequest) {
        return new ResponseEntity<>(new PointResponse(pointRequest.uuid(), 0L), HttpStatus.OK);
    }

    @GetMapping("/charge")
    public ResponseEntity<PointResponse> charge(@Valid @RequestBody PointRequest pointRequest) {
        return new ResponseEntity<>(new PointResponse(pointRequest.uuid(), pointRequest.point()), HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<PointResponse>> history(@Valid @RequestBody PointRequest pointRequest) {
        List<PointResponse> pointResponseList = new ArrayList<>();
        return new ResponseEntity<>(pointResponseList, HttpStatus.OK);
    }
}
