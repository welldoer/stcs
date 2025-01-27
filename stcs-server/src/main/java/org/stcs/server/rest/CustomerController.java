package org.stcs.server.rest;

import static org.stcs.server.constant.GlobalConstant.*;
import static org.stcs.server.protocol.STCSProtocolBuilder.*;

import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stcs.server.entity.CustomerEntity;
import org.stcs.server.service.CustomerService;

@RestController
@Slf4j
@RequestMapping(value = "api/v1/customers")
public class CustomerController extends AbstractRestController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService custInfoService) {
        this.customerService = custInfoService;
    }

    @GetMapping
    public ResponseEntity find() {
        final List<CustomerEntity> customerEntities = customerService.findAll();
        log.info("find result {}", customerEntities);
        return ResponseEntity.ok().body(buildResponseCollections(customerEntities));
    }

    @GetMapping(value = "/{custId}")
    public ResponseEntity findOne(@PathVariable int custId) {
        final CustomerEntity customerEntity = customerService.find(custId);
        log.info("find result {}", customerEntity);
        return ResponseEntity.ok().body(buildResponseCollections(Arrays.asList(customerEntity)));
    }

    @PostMapping
    public ResponseEntity add(@RequestBody String req) {
        final List<CustomerEntity> customerEntities = JSON.parseArray(req, CustomerEntity.class);
        long result = customerService.add(customerEntities);
        if (result > 0) {
            return ResponseEntity.ok().body(buildSuccess());
        }
        return ResponseEntity.ok(buildFailure(ERROR_1001, "add failure"));
    }

    @PutMapping(value = "/{custId}")
    public ResponseEntity update(@RequestBody JSONObject req, @PathVariable int custId) {
        final CustomerEntity customerEntity = customerService.find(custId);
        if (customerEntity == null) {
            return ResponseEntity.ok().body(buildFailure(ERROR_1005, "customer not found"));
        }
        final CustomerEntity newCustomerEntity = JSON.to(CustomerEntity.class, req);
        long result = customerService.update(newCustomerEntity);
        if (result > 0) {
            return ResponseEntity.ok(buildSuccess("update success"));
        }
        return ResponseEntity.ok(buildFailure(ERROR_1003, "update failure"));
    }

    @DeleteMapping(value = "/{custId}")
    public ResponseEntity delete(@PathVariable int custId) {
        long result = customerService.delete(custId);
        if (result > 0) {
            return ResponseEntity.ok(buildSuccess("delete success"));
        }
        return ResponseEntity.ok(buildFailure(ERROR_1002, "delete failure"));
    }
}
