import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TravelAgentListComponent } from './travel-agent-list.component';

describe('TravelAgentListComponent', () => {
  let component: TravelAgentListComponent;
  let fixture: ComponentFixture<TravelAgentListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TravelAgentListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TravelAgentListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
