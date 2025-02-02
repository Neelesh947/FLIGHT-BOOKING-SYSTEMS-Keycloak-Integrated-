import { TestBed } from '@angular/core/testing';

import { FlightoperationmanagerService } from './flightoperationmanager.service';

describe('FlightoperationmanagerService', () => {
  let service: FlightoperationmanagerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FlightoperationmanagerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
