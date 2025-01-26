import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlightOperationManagerComponent } from './flight-operation-manager.component';

describe('FlightOperationManagerComponent', () => {
  let component: FlightOperationManagerComponent;
  let fixture: ComponentFixture<FlightOperationManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FlightOperationManagerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FlightOperationManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
