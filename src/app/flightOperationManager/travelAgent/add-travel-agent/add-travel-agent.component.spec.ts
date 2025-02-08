import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddTravelAgentComponent } from './add-travel-agent.component';

describe('AddTravelAgentComponent', () => {
  let component: AddTravelAgentComponent;
  let fixture: ComponentFixture<AddTravelAgentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddTravelAgentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddTravelAgentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
