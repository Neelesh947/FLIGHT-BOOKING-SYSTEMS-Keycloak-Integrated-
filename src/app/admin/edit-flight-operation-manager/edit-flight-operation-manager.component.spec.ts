import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditFlightOperationManagerComponent } from './edit-flight-operation-manager.component';

describe('EditFlightOperationManagerComponent', () => {
  let component: EditFlightOperationManagerComponent;
  let fixture: ComponentFixture<EditFlightOperationManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditFlightOperationManagerComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditFlightOperationManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
