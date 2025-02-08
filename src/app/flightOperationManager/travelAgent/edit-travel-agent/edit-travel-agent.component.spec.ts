import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditTravelAgentComponent } from './edit-travel-agent.component';

describe('EditTravelAgentComponent', () => {
  let component: EditTravelAgentComponent;
  let fixture: ComponentFixture<EditTravelAgentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditTravelAgentComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditTravelAgentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
