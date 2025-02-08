import { TestBed } from '@angular/core/testing';

import { TravelAgentsService } from './travel-agents.service';

describe('TravelAgentsService', () => {
  let service: TravelAgentsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TravelAgentsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
