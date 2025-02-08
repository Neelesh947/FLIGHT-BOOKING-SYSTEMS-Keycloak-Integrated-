import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditAirportsComponent } from './edit-airports.component';

describe('EditAirportsComponent', () => {
  let component: EditAirportsComponent;
  let fixture: ComponentFixture<EditAirportsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditAirportsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditAirportsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
