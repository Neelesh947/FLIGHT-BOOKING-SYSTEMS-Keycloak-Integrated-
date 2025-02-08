import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddAirportsComponent } from './add-airports.component';

describe('AddAirportsComponent', () => {
  let component: AddAirportsComponent;
  let fixture: ComponentFixture<AddAirportsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddAirportsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddAirportsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
