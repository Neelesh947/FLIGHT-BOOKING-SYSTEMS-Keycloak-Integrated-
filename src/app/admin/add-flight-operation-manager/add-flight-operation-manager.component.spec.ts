import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddFlightOperationManagerComponent } from './add-flight-operation-manager.component';

describe('AddFlightOperationManagerComponent', () => {
  let component: AddFlightOperationManagerComponent;
  let fixture: ComponentFixture<AddFlightOperationManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddFlightOperationManagerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddFlightOperationManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
