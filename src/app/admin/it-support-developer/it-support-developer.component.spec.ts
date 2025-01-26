import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ItSupportDeveloperComponent } from './it-support-developer.component';

describe('ItSupportDeveloperComponent', () => {
  let component: ItSupportDeveloperComponent;
  let fixture: ComponentFixture<ItSupportDeveloperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ItSupportDeveloperComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ItSupportDeveloperComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
