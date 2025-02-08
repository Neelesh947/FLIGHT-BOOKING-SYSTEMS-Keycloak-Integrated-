import { TestBed } from '@angular/core/testing';

import { AirpottService } from './airpott.service';

describe('AirpottService', () => {
  let service: AirpottService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AirpottService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
